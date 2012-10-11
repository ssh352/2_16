package com.algoTrader.service.ib;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.algoTrader.entity.security.ForexFuture;
import com.algoTrader.entity.security.ForexFutureFamily;
import com.algoTrader.entity.security.ForexFutureImpl;
import com.algoTrader.entity.security.Future;
import com.algoTrader.entity.security.FutureFamily;
import com.algoTrader.entity.security.FutureImpl;
import com.algoTrader.entity.security.Security;
import com.algoTrader.entity.security.SecurityFamily;
import com.algoTrader.entity.security.StockOption;
import com.algoTrader.entity.security.StockOptionFamily;
import com.algoTrader.entity.security.StockOptionImpl;
import com.algoTrader.enumeration.Market;
import com.algoTrader.enumeration.OptionType;
import com.algoTrader.future.FutureSymbol;
import com.algoTrader.stockOption.StockOptionSymbol;
import com.algoTrader.util.MyLogger;
import com.algoTrader.util.RoundUtil;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;

public class IBSecurityRetrieverServiceImpl extends IBSecurityRetrieverServiceBase {

    private static final long serialVersionUID = 6446509772400405052L;

    private static Logger logger = MyLogger.getLogger(IBSecurityRetrieverServiceImpl.class.getName());
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddkkmmss");

    private IBClient client;
    private IBDefaultMessageHandler messageHandler;

    private Lock lock = new ReentrantLock();
    private Condition condition = this.lock.newCondition();

    private List<ContractDetails> contractDetailsList;

    private static int clientId = 3;

    @Override
    protected void handleRetrieve(int securityFamilyId) throws Exception {

        SecurityFamily securityFamily = getSecurityFamilyDao().get(securityFamilyId);

        // engage the retrieval process
        retrieveContractDetails(securityFamily);

        if (securityFamily instanceof StockOptionFamily) {
            retrieveStockOptions((StockOptionFamily) securityFamily);
        } else if (securityFamily instanceof ForexFutureFamily) {
            retrieveForexFutures((ForexFutureFamily) securityFamily);
        } else if (securityFamily instanceof FutureFamily) {
            retrieveFutures((FutureFamily) securityFamily);
        } else {
            throw new IllegalArgumentException(securityFamily.getClass() + " not allowed");
        }
    }

    private void retrieveStockOptions(StockOptionFamily family) throws Exception {

        Security underlying = family.getUnderlying();

        // get all current stockOptions (sorted by isin)
        Set<Security> existingStockOptions = new TreeSet<Security>(getComparator());
        existingStockOptions.addAll(getStockOptionDao().findStockOptionsBySecurityFamily(family.getId()));

        Set<StockOption> newStockOptions = new TreeSet<StockOption>();
        for (ContractDetails contractDetails : this.contractDetailsList) {

            StockOption stockOption = new StockOptionImpl();
            stockOption.setSecurityFamily(family);

            Contract contract = contractDetails.m_summary;
            OptionType type = "C".equals(contract.m_right) ? OptionType.CALL : OptionType.PUT;
            BigDecimal strike = RoundUtil.getBigDecimal(contract.m_strike, family.getScale());
            Date expiration = format.parse(contract.m_expiry + "130000");

            if (underlying.getSecurityFamily().getMarket().equals(Market.CBOE) || underlying.getSecurityFamily().getMarket().equals(Market.SOFFEX)) {
                expiration = DateUtils.addDays(expiration, 1);
            }

            final String isin = StockOptionSymbol.getIsin(family, expiration, type, strike);
            String symbol = StockOptionSymbol.getSymbol(family, expiration, type, strike);
            String ric = StockOptionSymbol.getRic(family, expiration, type, strike);
            String conid = String.valueOf(contract.m_conId);

            stockOption.setSymbol(symbol);
            stockOption.setIsin(isin);
            stockOption.setRic(ric);
            stockOption.setConid(conid);
            stockOption.setType(type);
            stockOption.setStrike(strike);
            stockOption.setExpiration(expiration);
            stockOption.setUnderlying(underlying);
            stockOption.setSecurityFamily(family);

            // ignore stockOptions that already exist
            if (!existingStockOptions.contains(stockOption)) {
                newStockOptions.add(stockOption);
            }
        }

        getStockOptionDao().create(newStockOptions);

        logger.debug("retrieved options for optionfamily: " + family.getName() + " " + newStockOptions);
    }

    private void retrieveFutures(FutureFamily family) throws Exception {

        Security underlying = family.getUnderlying();

        // get all current futures (sorted by isin)
        Set<Future> existingFutures = new TreeSet<Future>(getComparator());
        existingFutures.addAll(getFutureDao().findFuturesBySecurityFamily(family.getId()));

        Set<Future> newFutures = new TreeSet<Future>();
        for (ContractDetails contractDetails : this.contractDetailsList) {

            Future future = new FutureImpl();
            future.setSecurityFamily(family);

            Contract contract = contractDetails.m_summary;
            Date expiration = format.parse(contract.m_expiry + "130000");

            if (underlying.getSecurityFamily().getMarket().equals(Market.CBOE) || underlying.getSecurityFamily().getMarket().equals(Market.SOFFEX)) {
                expiration = DateUtils.addDays(expiration, 1);
            }

            String symbol = FutureSymbol.getSymbol(family, expiration);
            final String isin = FutureSymbol.getIsin(family, expiration);
            String ric = FutureSymbol.getRic(family, expiration);
            String conid = String.valueOf(contract.m_conId);

            future.setSymbol(symbol);
            future.setIsin(isin);
            future.setRic(ric);
            future.setConid(conid);
            future.setExpiration(expiration);
            future.setUnderlying(underlying);
            future.setSecurityFamily(family);

            // ignore futures that already exist
            if (!existingFutures.contains(future)) {
                newFutures.add(future);
            }
        }

        getFutureDao().create(newFutures);

        logger.debug("retrieved futures for futurefamily: " + family.getName() + " " + newFutures);
    }

    private void retrieveForexFutures(ForexFutureFamily family) throws Exception {

        Security underlying = family.getUnderlying();

        // get all current forexFutures (sorted by isin)
        Set<ForexFuture> existingForexFutures = new TreeSet<ForexFuture>(getComparator());
        existingForexFutures.addAll(getForexFutureDao().findForexFuturesBySecurityFamily(family.getId()));

        Set<ForexFuture> newForexFutures = new TreeSet<ForexFuture>();
        for (ContractDetails contractDetails : this.contractDetailsList) {

            ForexFuture forexFuture = new ForexFutureImpl();
            forexFuture.setSecurityFamily(family);

            Contract contract = contractDetails.m_summary;
            Date expiration = format.parse(contract.m_expiry + "130000");

            if (underlying.getSecurityFamily().getMarket().equals(Market.CBOE) || underlying.getSecurityFamily().getMarket().equals(Market.SOFFEX)) {
                expiration = DateUtils.addDays(expiration, 1);
            }

            String symbol = FutureSymbol.getSymbol(family, expiration);
            final String isin = FutureSymbol.getIsin(family, expiration);
            String ric = FutureSymbol.getRic(family, expiration);
            String conid = String.valueOf(contract.m_conId);

            forexFuture.setSymbol(symbol);
            forexFuture.setIsin(isin);
            forexFuture.setRic(ric);
            forexFuture.setConid(conid);
            forexFuture.setExpiration(expiration);
            forexFuture.setUnderlying(underlying);
            forexFuture.setSecurityFamily(family);
            forexFuture.setBaseCurrency(family.getBaseCurrency());

            // ignore forexFutures that already exist
            if (!existingForexFutures.contains(forexFuture)) {
                newForexFutures.add(forexFuture);
            }
        }

        getForexFutureDao().create(newForexFutures);

        logger.debug("retrieved forexFutures for forexFuturefamily: " + family.getName() + " " + newForexFutures);
    }

    private void retrieveContractDetails(SecurityFamily family) throws InterruptedException {

        this.contractDetailsList = new ArrayList<ContractDetails>();

        IBSecurityRetrieverServiceImpl.this.lock.lock();

        try {

            int requestId = IBIdGenerator.getInstance().getNextRequestId();
            Contract contract = new Contract();
            contract.m_currency = family.getCurrency().toString();
            contract.m_symbol = family.getBaseSymbol();
            contract.m_exchange = IBMarketConverter.marketToString(family.getMarket());

            if (family instanceof StockOptionFamily) {
                contract.m_secType = "OPT";
            } else if (family instanceof FutureFamily) {
                contract.m_secType = "FUT";
            }

            this.client.reqContractDetails(requestId, contract);

            this.condition.await();

        } finally {
            IBSecurityRetrieverServiceImpl.this.lock.unlock();
        }
    }

    private Comparator<Security> getComparator() {

        // comparator based on isin
        Comparator<Security> comparator = new Comparator<Security>() {
            @Override
            public int compare(Security o1, Security o2) {
                return o1.getIsin().compareTo(o2.getIsin());
            }
        };
        return comparator;
    }

    @Override
    protected void handleInit() throws java.lang.Exception {

        this.messageHandler = new IBDefaultMessageHandler(clientId) {

            @Override
            public void contractDetails(int reqId, ContractDetails contractDetails) {

                IBSecurityRetrieverServiceImpl.this.contractDetailsList.add(contractDetails);
            }

            @Override
            public void contractDetailsEnd(int reqId) {

                IBSecurityRetrieverServiceImpl.this.lock.lock();

                try {
                    IBSecurityRetrieverServiceImpl.this.condition.signalAll();
                } finally {
                    IBSecurityRetrieverServiceImpl.this.lock.unlock();
                }
            }

            @Override
            public void connectionClosed() {

                super.connectionClosed();

                IBSecurityRetrieverServiceImpl.this.client.connect();
            }
        };

        this.client = getIBClientFactory().getClient(clientId, this.messageHandler);

        this.client.connect();
    }
}
