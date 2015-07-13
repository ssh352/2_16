/***********************************************************************************
 * AlgoTrader Enterprise Trading Framework
 *
 * Copyright (C) 2014 AlgoTrader GmbH - All rights reserved
 *
 * All information contained herein is, and remains the property of AlgoTrader GmbH.
 * The intellectual and technical concepts contained herein are proprietary to
 * AlgoTrader GmbH. Modification, translation, reverse engineering, decompilation,
 * disassembly or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from AlgoTrader GmbH
 *
 * Fur detailed terms and conditions consult the file LICENSE.txt or contact
 *
 * AlgoTrader GmbH
 * Badenerstrasse 16
 * 8004 Zurich
 ***********************************************************************************/
package ch.algotrader.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.algotrader.accounting.PositionTrackerImpl;
import ch.algotrader.config.CommonConfig;
import ch.algotrader.config.CoreConfig;
import ch.algotrader.dao.ClosePositionVOProducer;
import ch.algotrader.dao.ExpirePositionVOProducer;
import ch.algotrader.dao.PositionDao;
import ch.algotrader.dao.TransactionDao;
import ch.algotrader.dao.security.SecurityDao;
import ch.algotrader.dao.strategy.StrategyDao;
import ch.algotrader.entity.Position;
import ch.algotrader.entity.Transaction;
import ch.algotrader.entity.marketData.MarketDataEvent;
import ch.algotrader.entity.security.Combination;
import ch.algotrader.entity.security.Future;
import ch.algotrader.entity.security.Option;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.security.SecurityFamily;
import ch.algotrader.entity.strategy.Strategy;
import ch.algotrader.entity.trade.Order;
import ch.algotrader.entity.trade.OrderStatus;
import ch.algotrader.enumeration.Side;
import ch.algotrader.enumeration.Status;
import ch.algotrader.enumeration.TransactionType;
import ch.algotrader.esper.Engine;
import ch.algotrader.esper.EngineManager;
import ch.algotrader.esper.callback.TradeCallback;
import ch.algotrader.event.dispatch.EventDispatcher;
import ch.algotrader.option.OptionUtil;
import ch.algotrader.util.RoundUtil;
import ch.algotrader.util.collection.Pair;
import ch.algotrader.vo.ClosePositionVO;
import ch.algotrader.vo.ExpirePositionVO;

/**
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @version $Revision$ $Date$
 */
@Transactional
public class PositionServiceImpl implements PositionService {

    private static final Logger LOGGER = LogManager.getLogger(PositionServiceImpl.class);

    private final CommonConfig commonConfig;

    private final CoreConfig coreConfig;

    private final TransactionService transactionService;

    private final MarketDataService marketDataService;

    private final OrderService orderService;

    private final LocalLookupService localLookupService;

    private final PositionDao positionDao;

    private final SecurityDao securityDao;

    private final StrategyDao strategyDao;

    private final TransactionDao transactionDao;

    private final EventDispatcher eventDispatcher;

    private final EngineManager engineManager;

    private final Engine serverEngine;

    public PositionServiceImpl(final CommonConfig commonConfig,
            final CoreConfig coreConfig,
            final TransactionService transactionService,
            final MarketDataService marketDataService,
            final OrderService orderService,
            final LocalLookupService localLookupService,
            final PositionDao positionDao,
            final SecurityDao securityDao,
            final StrategyDao strategyDao,
            final TransactionDao transactionDao,
            final EventDispatcher eventDispatcher,
            final EngineManager engineManager,
            final Engine serverEngine) {

        Validate.notNull(commonConfig, "CommonConfig is null");
        Validate.notNull(coreConfig, "CoreConfig is null");
        Validate.notNull(transactionService, "TransactionService is null");
        Validate.notNull(marketDataService, "MarketDataService is null");
        Validate.notNull(orderService, "OrderService is null");
        Validate.notNull(localLookupService, "LocalLookupService is null");
        Validate.notNull(positionDao, "PositionDao is null");
        Validate.notNull(securityDao, "SecurityDao is null");
        Validate.notNull(strategyDao, "StrategyDao is null");
        Validate.notNull(transactionDao, "TransactionDao is null");
        Validate.notNull(eventDispatcher, "PlatformEventDispatcher is null");
        Validate.notNull(engineManager, "EngineManager is null");
        Validate.notNull(serverEngine, "Engine is null");

        this.commonConfig = commonConfig;
        this.coreConfig = coreConfig;
        this.transactionService = transactionService;
        this.marketDataService = marketDataService;
        this.orderService = orderService;
        this.localLookupService = localLookupService;
        this.positionDao = positionDao;
        this.securityDao = securityDao;
        this.strategyDao = strategyDao;
        this.transactionDao = transactionDao;
        this.eventDispatcher = eventDispatcher;
        this.engineManager = engineManager;
        this.serverEngine = serverEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeAllPositionsByStrategy(final String strategyName, final boolean unsubscribe) {

        Validate.notEmpty(strategyName, "Strategy name is empty");

        for (Position position : this.positionDao.findOpenPositionsByStrategy(strategyName)) {
            if (position.isOpen()) {
                closePosition(position.getId(), unsubscribe);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closePosition(final long positionId, final boolean unsubscribe) {

        final Position position = this.positionDao.get(positionId);
        if (position == null) {
            throw new IllegalArgumentException("position with id " + positionId + " does not exist");
        }

        Security security = position.getSecurity();

        if (position.isOpen()) {

            // handle Combinations by the combination service
            if (security instanceof Combination) {
                throw new ServiceException("Cannot close Combination position");
            } else {
                reduceOrClosePosition(position, position.getQuantity(), unsubscribe);
            }

        } else {

            // if there was no open position but unsubscribe was requested do that anyway
            if (unsubscribe) {
                this.marketDataService.unsubscribe(position.getStrategy().getName(), security.getId());
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Position createNonTradeablePosition(final String strategyName, final long securityId, final long quantity) {

        Validate.notEmpty(strategyName, "Strategy name is empty");

        Security security = this.securityDao.get(securityId);
        Strategy strategy = this.strategyDao.findByName(strategyName);

        if (security.getSecurityFamily().isTradeable()) {
            throw new ServiceException(security + " is tradeable, can only creat non-tradeable positions");
        }

        Position position = Position.Factory.newInstance();
        position.setQuantity(quantity);

        // associate strategy and security
        position.setStrategy(strategy);
        position.setSecurity(security);

        this.positionDao.save(position);

        // reverse-associate the security (after position has received an id)
        security.getPositions().add(position);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("created non-tradeable position on {} for strategy {} quantity {}", security, strategyName, quantity);
        }

        return position;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Position modifyNonTradeablePosition(final long positionId, final long quantity) {

        Position position = this.positionDao.getLocked(positionId);
        if (position == null) {
            throw new IllegalArgumentException("position with id " + positionId + " does not exist");
        }

        position.setQuantity(quantity);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("modified non-tradeable position {} new quantity {}", positionId, quantity);
        }

        return position;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNonTradeablePosition(final long positionId, final boolean unsubscribe) {

        Position position = this.positionDao.get(positionId);
        if (position == null) {
            throw new IllegalArgumentException("position with id " + positionId + " does not exist");
        }

        Security security = position.getSecurity();

        if (security.getSecurityFamily().isTradeable()) {
            throw new ServiceException(security + " is tradeable, can only delete non-tradeable positions");
        }

        ClosePositionVO closePositionVO = ClosePositionVOProducer.INSTANCE.convert(position);

        // propagate the ClosePosition event
        this.eventDispatcher.sendEvent(position.getStrategy().getName(), closePositionVO);

        // remove the association
        position.getSecurity().removePositions(position);

        this.positionDao.delete(position);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("deleted non-tradeable position {} on {} for strategy {}", position.getId(), security, position.getStrategy().getName());
        }

        // unsubscribe if necessary
        if (unsubscribe) {
            this.marketDataService.unsubscribe(position.getStrategy().getName(), position.getSecurity().getId());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reducePosition(final long positionId, final long quantity) {

        Position position = this.positionDao.get(positionId);
        if (position == null) {
            throw new IllegalArgumentException("position with id " + positionId + " does not exist");
        }

        if (Math.abs(quantity) > Math.abs(position.getQuantity())) {
            throw new ServiceException("position reduction of " + quantity + " for position " + position.getId() + " is greater than current quantity " + position.getQuantity());
        } else {
            reduceOrClosePosition(position, quantity, false);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transferPosition(final long positionId, final String targetStrategyName) {

        Validate.notEmpty(targetStrategyName, "Target strategy name is empty");

        Position position = this.positionDao.get(positionId);
        if (position == null) {
            throw new IllegalArgumentException("position with id " + positionId + " does not exist");
        }

        Strategy targetStrategy = this.strategyDao.findByName(targetStrategyName);
        Security security = position.getSecurity();
        SecurityFamily family = security.getSecurityFamily();
        MarketDataEvent marketDataEvent = this.localLookupService.getCurrentMarketDataEvent(security.getId());
        BigDecimal price = RoundUtil.getBigDecimal(position.getMarketPrice(marketDataEvent), family.getScale());

        // debit transaction
        Transaction debitTransaction = Transaction.Factory.newInstance();
        debitTransaction.setUuid(UUID.randomUUID().toString());
        debitTransaction.setDateTime(this.engineManager.getCurrentEPTime());
        debitTransaction.setQuantity(-position.getQuantity());
        debitTransaction.setPrice(price);
        debitTransaction.setCurrency(family.getCurrency());
        debitTransaction.setType(TransactionType.TRANSFER);
        debitTransaction.setSecurity(security);
        debitTransaction.setStrategy(position.getStrategy());

        // persiste the transaction
        this.transactionService.persistTransaction(debitTransaction);

        // credit transaction
        Transaction creditTransaction = Transaction.Factory.newInstance();
        creditTransaction.setUuid(UUID.randomUUID().toString());
        creditTransaction.setDateTime(this.engineManager.getCurrentEPTime());
        creditTransaction.setQuantity(position.getQuantity());
        creditTransaction.setPrice(price);
        creditTransaction.setCurrency(family.getCurrency());
        creditTransaction.setType(TransactionType.TRANSFER);
        creditTransaction.setSecurity(security);
        creditTransaction.setStrategy(targetStrategy);

        // persiste the transaction
        this.transactionService.persistTransaction(creditTransaction);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void expirePositions() {

        Date date = this.engineManager.getCurrentEPTime();
        Collection<Position> positions = this.positionDao.findExpirablePositions(date);

        for (Position position : positions) {
            expirePosition(position);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String resetPositions() {

        Collection<Transaction> transactions = this.transactionDao.findAllTradesInclSecurity();

        // process all transactions to establis current position states
        Map<Pair<Security, Strategy>, Position> positionMap = new HashMap<>();
        for (Transaction transaction : transactions) {

            // crate a position if we come across a security for the first time
            Position position = positionMap.get(new Pair<>(transaction.getSecurity(), transaction.getStrategy()));
            if (position == null) {
                position = PositionTrackerImpl.INSTANCE.processFirstTransaction(transaction);
                positionMap.put(new Pair<>(position.getSecurity(), position.getStrategy()), position);
            } else {
                PositionTrackerImpl.INSTANCE.processTransaction(position, transaction);
            }
        }

        // update positions
        StringBuilder buffer = new StringBuilder();
        for (Position targetOpenPosition : positionMap.values()) {

            Position actualOpenPosition = this.positionDao.findBySecurityAndStrategy(targetOpenPosition.getSecurity().getId(), targetOpenPosition.getStrategy().getName());

            // create if it does not exist
            if (actualOpenPosition == null) {

                String warning = "position on security " + targetOpenPosition.getSecurity() + " strategy " + targetOpenPosition.getStrategy() + " quantity " + targetOpenPosition.getQuantity()
                        + " does not exist";
                LOGGER.warn(warning);
                buffer.append(warning + "\n");

            } else {

                // check quantity
                if (actualOpenPosition.getQuantity() != targetOpenPosition.getQuantity()) {

                    long existingQty = actualOpenPosition.getQuantity();
                    actualOpenPosition.setQuantity(targetOpenPosition.getQuantity());

                    String warning = "adjusted quantity of position " + actualOpenPosition.getId() + " from " + existingQty + " to " + targetOpenPosition.getQuantity();
                    LOGGER.warn(warning);
                    buffer.append(warning + "\n");
                }

                // check cost
                if (actualOpenPosition.getCost() != targetOpenPosition.getCost()) {

                    double existingCost = actualOpenPosition.getCost();
                    actualOpenPosition.setCost(targetOpenPosition.getCost());

                    String warning = "adjusted cost of position " + actualOpenPosition.getId() + " from " + existingCost + " to " + targetOpenPosition.getCost();
                    LOGGER.warn(warning);
                    buffer.append(warning + "\n");
                }

                // check realizedPL
                if (actualOpenPosition.getRealizedPL() != targetOpenPosition.getRealizedPL()) {

                    double existingRealizedPL = actualOpenPosition.getRealizedPL();
                    actualOpenPosition.setRealizedPL(targetOpenPosition.getRealizedPL());

                    String warning = "adjusted realizedPL of position " + actualOpenPosition.getId() + " from " + existingRealizedPL + " to " + targetOpenPosition.getRealizedPL();
                    LOGGER.warn(warning);
                    buffer.append(warning + "\n");
                }
            }
        }

        return buffer.toString();

    }

    private void reduceOrClosePosition(final Position position, long quantity, final boolean unsubscribe) {

        Strategy strategy = position.getStrategy();
        Security security = position.getSecurity();

        Side side = (position.getQuantity() > 0) ? Side.SELL : Side.BUY;

        Order order = this.orderService.createOrderByOrderPreference(this.coreConfig.getDefaultOrderPreference());

        order.setStrategy(strategy);
        order.setSecurity(security);
        order.setQuantity(Math.abs(quantity));
        order.setSide(side);

        // unsubscribe is requested / notify non-full executions in live-trading
        if (this.commonConfig.isSimulation()) {
            if (unsubscribe) {
                this.marketDataService.unsubscribe(order.getStrategy().getName(), order.getSecurity().getId());
            }
        } else {
            this.serverEngine.addTradeCallback(Collections.singleton(order), new TradeCallback(true) {
                @Override
                public void onTradeCompleted(List<OrderStatus> orderStati) throws Exception {
                    if (unsubscribe) {
                        for (OrderStatus orderStatus : orderStati) {
                            Order order = orderStatus.getOrder();
                            if (Status.EXECUTED.equals(orderStatus.getStatus())) {
                                // use ServiceLocator because TradeCallback is executed in a new thread
                                PositionServiceImpl.this.marketDataService.unsubscribe(order.getStrategy().getName(), order.getSecurity().getId());
                            }
                        }
                    }
                }
            });
        }

        this.orderService.sendOrder(order);
    }

    private void expirePosition(Position position) {

        Security security = position.getSecurity();

        ExpirePositionVO expirePositionEvent = ExpirePositionVOProducer.INSTANCE.convert(position);

        Transaction transaction = Transaction.Factory.newInstance();
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setDateTime(this.engineManager.getCurrentEPTime());
        transaction.setType(TransactionType.EXPIRATION);
        transaction.setQuantity(-position.getQuantity());
        transaction.setSecurity(security);
        transaction.setStrategy(position.getStrategy());
        transaction.setCurrency(security.getSecurityFamily().getCurrency());

        if (security instanceof Option) {

            Option option = (Option) security;
            int scale = security.getSecurityFamily().getScale();
            double underlyingSpot = this.localLookupService.getCurrentValueDouble(security.getUnderlying().getId());
            double intrinsicValue = OptionUtil.getIntrinsicValue(option, underlyingSpot);
            BigDecimal price = RoundUtil.getBigDecimal(intrinsicValue, scale);
            transaction.setPrice(price);

        } else if (security instanceof Future) {

            BigDecimal price = this.localLookupService.getCurrentValue(security.getUnderlying().getId());
            transaction.setPrice(price);

        } else {
            throw new IllegalArgumentException("Expiration not allowed for " + security.getClass().getName());
        }

        // perisite the transaction
        this.transactionService.persistTransaction(transaction);

        // unsubscribe the security
        this.marketDataService.unsubscribe(position.getStrategy().getName(), security.getId());

        // propagate the ExpirePosition event
        this.eventDispatcher.sendEvent(position.getStrategy().getName(), expirePositionEvent);
    }
}
