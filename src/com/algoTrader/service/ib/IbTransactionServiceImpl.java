package com.algoTrader.service.ib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.algoTrader.entity.Order;
import com.algoTrader.entity.PartialOrder;
import com.algoTrader.entity.Security;
import com.algoTrader.entity.Tick;
import com.algoTrader.entity.Transaction;
import com.algoTrader.entity.TransactionImpl;
import com.algoTrader.enumeration.OrderStatus;
import com.algoTrader.enumeration.TransactionType;
import com.algoTrader.service.TickServiceException;
import com.algoTrader.util.MyLogger;
import com.algoTrader.util.PropertiesUtil;
import com.algoTrader.util.RoundUtil;
import com.ib.client.AnyWrapper;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.Execution;
import com.ib.client.OrderState;

public class IbTransactionServiceImpl extends IbTransactionServiceBase implements InitializingBean {

    private static Logger logger = MyLogger.getLogger(IbTransactionServiceImpl.class.getName());

    private static boolean ibEnabled = "IB".equals(PropertiesUtil.getProperty("marketChannel"));
    private static String[] bidAskSpreadPositions = PropertiesUtil.getProperty("bidAskSpreadPositions").split("\\s");

    private static int port = PropertiesUtil.getIntProperty("ib.port");
    private static String group = PropertiesUtil.getProperty("ib.group");
    private static String openMethod = PropertiesUtil.getProperty("ib.openMethod");
    private static String closeMethod = PropertiesUtil.getProperty("ib.closeMethod");
    private static int timeout = PropertiesUtil.getIntProperty("ib.timeout");

    private EClientSocket client;
    private Lock lock = new ReentrantLock();
    private Condition condition = this.lock.newCondition();

    private Map<Integer, PartialOrder> partialOrdersMap = new HashMap<Integer, PartialOrder>();
    private Map<Integer, Boolean> executedMap = new HashMap<Integer, Boolean>();
    private Map<Integer, Boolean> deletedMap = new HashMap<Integer, Boolean>();

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd  hh:mm:ss");
    private static int clientId = 0;

    public void afterPropertiesSet() throws Exception {

        init();
    }

    protected void handleInit() {

        if (!ibEnabled)
            return;

        AnyWrapper wrapper = new DefaultWrapper() {

            @Override
            public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

                logger.debug("orderId: " + orderId +
                        " orderStatus: " + status +
                        " filled: " + filled +
                        " remaining: " + remaining +
                        " avgFillPrice: " + avgFillPrice +
                        " permId: " + permId +
                        " lastFillPrice: " + lastFillPrice +
                        " whyHeld: " + whyHeld);

                IbTransactionServiceImpl.this.lock.lock();
                try {

                    PartialOrder partialOrder = IbTransactionServiceImpl.this.partialOrdersMap.get(orderId);

                    partialOrder.setExecutedQuantity(filled);

                    if ((filled > 0) && ("Submitted".equals(status) || "PendingSubmit".equals(status))) {

                        partialOrder.setStatus(OrderStatus.PARTIALLY_EXECUTED);

                    } else if ("Filled".equals(status) && (partialOrder.getCommission() != 0)) {

                        partialOrder.setStatus(OrderStatus.EXECUTED);

                        IbTransactionServiceImpl.this.executedMap.put(orderId, true);
                        IbTransactionServiceImpl.this.condition.signalAll();

                    } else if ("Cancelled".equals(status)) {

                        partialOrder.setStatus(OrderStatus.CANCELED);

                        IbTransactionServiceImpl.this.deletedMap.put(orderId, true);
                        IbTransactionServiceImpl.this.condition.signalAll();
                    }

                } finally {
                    IbTransactionServiceImpl.this.lock.unlock();
                }
            }

            public void openOrder(int orderId, Contract contract, com.ib.client.Order iBOrder, OrderState orderState) {

                double commission = orderState.m_commission == Double.MAX_VALUE ? 0 : orderState.m_commission;
                logger.debug("orderId: " + orderId + " openOrder: commission: " + commission);

                IbTransactionServiceImpl.this.lock.lock();
                try {

                    PartialOrder partialOrder = IbTransactionServiceImpl.this.partialOrdersMap.get(orderId);
                    partialOrder.setCommission(commission);

                } finally {
                    IbTransactionServiceImpl.this.lock.unlock();
                }
            }

            public void nextValidId(int orderId) {

                logger.debug("nextValidId: " + orderId);

                RequestIdManager.getInstance().initializeOrderId(orderId);
            }

            public void execDetails(int requestId, Contract contract, Execution execution) {

                logger.debug("orderId: " + execution.m_orderId +
                        " execId: " + execution.m_execId +
                        " time: " + execution.m_time +
                        " acctNumber: " + execution.m_acctNumber +
                        " shares: " + execution.m_shares +
                        " price: " + execution.m_price +
                        " permId: " + execution.m_permId +
                        " cumQty: " + execution.m_cumQty +
                        " avgPrice: " + execution.m_avgPrice);

                IbTransactionServiceImpl.this.lock.lock();
                try {

                    // if the execution does not represent a internal transfer create a transaction
                    if (!execution.m_execId.startsWith("F") && !execution.m_execId.startsWith("U")) {

                        PartialOrder partialOrder = IbTransactionServiceImpl.this.partialOrdersMap.get(execution.m_orderId);

                        Transaction transaction = new TransactionImpl();
                        transaction.setDateTime(format.parse(execution.m_time));
                        transaction.setNumber(String.valueOf(execution.m_permId));
                        transaction.setQuantity(execution.m_shares);
                        transaction.setPrice(RoundUtil.getBigDecimal(execution.m_price));

                        partialOrder.addTransaction(transaction);
                    }

                } catch (ParseException e) {
                    logger.error("illegal time format ", e);
                } finally {
                    IbTransactionServiceImpl.this.lock.unlock();
                }
            }

            public void error(int id, int code, String errorMsg) {

                if (code == 202) {
                    // do nothing, since we probably cancelled the order ourself
                } else {
                    super.error(id, code, errorMsg);
                }
            }
        };

        this.client = new EClientSocket(wrapper);
        this.client.eConnect(null, port, clientId);
    }

    protected void handleExecuteExternalTransaction(Order order) throws Exception {

        getPartialOrder(order);

        Tick tick = null;
        for (String bidAskSpreadPosition : bidAskSpreadPositions) {

            if (bidAskSpreadPosition.equals(bidAskSpreadPositions[0])) {

                tick = getValidTick(order);
            }

            PartialOrder partialOrder = order.getCurrentPartialOrder();

            placeOrModifyPartialOrder(partialOrder, Double.valueOf(bidAskSpreadPosition), tick);

            if (OrderStatus.OPEN.equals(partialOrder.getStatus())) {

                // nothing went through, so try next higher bidAskSpreadPosition
                continue;

            } else if (OrderStatus.PARTIALLY_EXECUTED.equals(partialOrder.getStatus())) {

                // try to cancel, if successfull reset the partialOrder
                // otherwise the order must have been executed in the meantime
                if (cancelPartialOrder(partialOrder)) {

                    distributeCommissions(partialOrder);

                    // cancel sucessfull so reset the order
                    getPartialOrder(order);
                    continue;

                } else {

                    // order did EXECUTE after beeing cancelled, so we are done!
                    distributeCommissions(partialOrder);
                    break;
                }

            } else if (OrderStatus.EXECUTED.equals(partialOrder.getStatus())) {

                // we are done!
                distributeCommissions(partialOrder);

                break;
            }
        }

        cancelRemainingOrder(order);
    }

    private Tick getValidTick(Order order) throws TransformerException, ParseException, InterruptedException {

        Security security = order.getSecurity();
        TransactionType transactionType = order.getTransactionType();
        long requestedQuantity = order.getRequestedQuantity();

        // only validate price and volum the first time, because our
        // orders will show up in the orderbook as well
        Tick tick;
        while (true) {

            tick = getIbTickService().retrieveTick(order.getSecurity());

            // validity check (volume and bid/ask spread)
            try {
                tick.validate();
                break;

            } catch (TickServiceException e) {

                logger.warn(e.getMessage());

                // wait a little then try again
                Thread.sleep(timeout);
            }
        }

        // validity check (available volume)
        if (TransactionType.BUY.equals(transactionType) && tick.getVolAsk() < requestedQuantity) {
            logger.warn("available volume (" + tick.getVolAsk() + ") is smaler than requested quantity (" + requestedQuantity + ") for a order on " + security.getIsin());
        } else if (TransactionType.SELL.equals(transactionType) && tick.getVolBid() < requestedQuantity) {
            logger.warn("available volume (" + tick.getVolBid() + ") is smaler than requested quantity (" + requestedQuantity + ") for a order on " + security.getIsin());
        }

        return tick;
    }

    private void getPartialOrder(Order order) {

        PartialOrder partialOrder = order.createPartialOrder();

        partialOrder.setOrderId(RequestIdManager.getInstance().getNextOrderId());

        this.partialOrdersMap.put(partialOrder.getOrderId(), partialOrder);
    }

    private void placeOrModifyPartialOrder(PartialOrder partialOrder, double bidAskSpreadPosition, Tick tick) {

        this.executedMap.put(partialOrder.getOrderId(), false);

        Contract contract = IbUtil.getContract(partialOrder.getParentOrder().getSecurity());

        com.ib.client.Order ibOrder = new com.ib.client.Order();
        ibOrder.m_action = partialOrder.getParentOrder().getTransactionType().getValue();
        ibOrder.m_totalQuantity = (int) partialOrder.getRequestedQuantity();
        ibOrder.m_orderType = "LMT";
        ibOrder.m_lmtPrice = getPrice(partialOrder.getParentOrder(), bidAskSpreadPosition, tick.getBid().doubleValue(), tick.getAsk().doubleValue());
        ibOrder.m_faGroup = group;

        if (TransactionType.SELL.equals(partialOrder.getParentOrder().getTransactionType())) {
            ibOrder.m_faMethod = openMethod;
        }
        if (TransactionType.BUY.equals(partialOrder.getParentOrder().getTransactionType())) {
            ibOrder.m_faMethod = closeMethod;
            ibOrder.m_faPercentage = "-100";
        }

        this.lock.lock();

        try {

            this.client.placeOrder(partialOrder.getOrderId(), contract, ibOrder);

            logger.debug("orderId: " + partialOrder.getOrderId() +
                    " placeOrder for quantity: " + partialOrder.getRequestedQuantity()
                    + " limit: " + ibOrder.m_lmtPrice
                    + " bidAskSpreadPosition: " + bidAskSpreadPosition);

            while (!this.executedMap.get(partialOrder.getOrderId())) {
                if (!this.condition.await(timeout, TimeUnit.MILLISECONDS))
                    break;
            }

        } catch (InterruptedException e) {
            logger.error("problem placing order", e);
        } finally {
            this.lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private void distributeCommissions(PartialOrder partialOrder) {

        if (partialOrder.getExecutedQuantity() > 0) {
            Collection<Transaction> transactions = partialOrder.getTransactions();
            for (Transaction transaction : transactions) {
                double transactionCommission = partialOrder.getCommission() / partialOrder.getExecutedQuantity() * transaction.getQuantity();
                transaction.setCommission(RoundUtil.getBigDecimal(transactionCommission));
            }
        }
    }

    private boolean cancelPartialOrder(PartialOrder partialOrder) {

        this.deletedMap.put(partialOrder.getOrderId(), false);

        this.lock.lock();

        try {

            this.client.cancelOrder(partialOrder.getOrderId());

            logger.debug("orderId: " + partialOrder.getOrderId() + " cancelOrder");

            while (!this.deletedMap.get(partialOrder.getOrderId()) && !this.executedMap.get(partialOrder.getOrderId())) {
                this.condition.await();
            }

            if (OrderStatus.CANCELED.equals(partialOrder.getStatus())) {
                logger.debug("orderId: " + partialOrder.getOrderId() + " has been canceled");
                return true;

            } else if (OrderStatus.EXECUTED.equals(partialOrder.getStatus())) {
                logger.debug("orderId: " + partialOrder.getOrderId() + " has been executed after trying to cancel");
                return false;

            } else {
                throw new IbTickServiceException("orderId: " + partialOrder.getOrderId() + " unappropriate order status: " + partialOrder.getStatus());
            }

        } catch (InterruptedException e) {
            throw new IbTickServiceException("problem canceling order", e);
        } finally {
            this.lock.unlock();
        }
    }

    private void cancelRemainingOrder(Order order) {

        // if order did not execute fully, cancel the rest
        if (OrderStatus.PARTIALLY_EXECUTED.equals(order.getStatus())) {

            cancelPartialOrder(order.getCurrentPartialOrder());
            order.setStatus(OrderStatus.CANCELED);

            logger.warn("orderid: " + order.getNumber() + " did not execute fully, requestedQuantity: " + order.getRequestedQuantity() + " executedQuantity: "
                    + order.getPartialOrderExecutedQuantity());
        }
    }
}
