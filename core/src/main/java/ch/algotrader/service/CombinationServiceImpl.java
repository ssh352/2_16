/***********************************************************************************
 * AlgoTrader Enterprise Trading Framework
 *
 * Copyright (C) 2015 AlgoTrader GmbH - All rights reserved
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
 * Aeschstrasse 6
 * 8834 Schindellegi
 ***********************************************************************************/
package ch.algotrader.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.algotrader.config.CommonConfig;
import ch.algotrader.dao.SubscriptionDao;
import ch.algotrader.dao.security.CombinationDao;
import ch.algotrader.dao.security.ComponentDao;
import ch.algotrader.dao.security.SecurityDao;
import ch.algotrader.dao.security.SecurityFamilyDao;
import ch.algotrader.entity.Subscription;
import ch.algotrader.entity.security.Combination;
import ch.algotrader.entity.security.Component;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.security.SecurityFamily;
import ch.algotrader.enumeration.CombinationType;
import ch.algotrader.enumeration.InitializingServiceType;
import ch.algotrader.esper.Engine;
import ch.algotrader.vo.InsertComponentEventVO;

/**
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 */
@Transactional(propagation = Propagation.SUPPORTS)
@InitializationPriority(InitializingServiceType.CORE)
public class CombinationServiceImpl implements CombinationService, InitializingServiceI {

    private static final Logger LOGGER = LogManager.getLogger(CombinationServiceImpl.class);

    private final CommonConfig commonConfig;

    private final MarketDataService marketDataService;

    private final SubscriptionDao subscriptionDao;

    private final CombinationDao combinationDao;

    private final SecurityDao securityDao;

    private final ComponentDao componentDao;

    private final SecurityFamilyDao securityFamilyDao;

    private final Engine serverEngine;

    public CombinationServiceImpl(final CommonConfig commonConfig,
            final MarketDataService marketDataService,
            final SubscriptionDao subscriptionDao,
            final CombinationDao combinationDao,
            final SecurityDao securityDao,
            final ComponentDao componentDao,
            final SecurityFamilyDao securityFamilyDao,
            final Engine serverEngine) {

        Validate.notNull(commonConfig, "CommonConfig is null");
        Validate.notNull(marketDataService, "MarketDataService is null");
        Validate.notNull(subscriptionDao, "SubscriptionDao is null");
        Validate.notNull(combinationDao, "CombinationDao is null");
        Validate.notNull(securityDao, "SecurityDao is null");
        Validate.notNull(componentDao, "ComponentDao is null");
        Validate.notNull(securityFamilyDao, "SecurityFamilyDao is null");
        Validate.notNull(serverEngine, "Engine is null");

        this.commonConfig = commonConfig;
        this.marketDataService = marketDataService;
        this.combinationDao = combinationDao;
        this.subscriptionDao = subscriptionDao;
        this.securityDao = securityDao;
        this.componentDao = componentDao;
        this.securityFamilyDao = securityFamilyDao;
        this.serverEngine = serverEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Combination createCombination(final CombinationType type, final long securityFamilyId) {

        Validate.notNull(type, "Type is null");

        // create the combination
        Combination combination = Combination.Factory.newInstance();

        // set the uuid since combinations have no other unique identifier
        combination.setUuid(UUID.randomUUID().toString());
        combination.setCombinationType(type);

        // attach the security family
        SecurityFamily securityFamily = this.securityFamilyDao.get(securityFamilyId);

        // associate the security family
        combination.setSecurityFamily(securityFamily);

        // save to DB
        this.combinationDao.save(combination);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created combination {}", combination);
        }

        return combination;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Combination createCombination(final CombinationType type, final long securityFamilyId, final long underlyingId) {

        Validate.notNull(type, "Type is null");

        Security underlying = this.securityDao.load(underlyingId);
        if (underlying == null) {
            throw new IllegalArgumentException("underlying does not exist: " + underlyingId);
        }

        Combination combination = createCombination(type, securityFamilyId);
        combination.setUnderlying(underlying);

        return combination;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCombination(final long combinationId) {

        Combination combination = this.combinationDao.get(combinationId);

        if (combination == null) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("combination does not exist: {}", combinationId);
            }

        } else {

            // unsubscribe potential subscribers
            List<Subscription> subscriptions = this.subscriptionDao.findBySecurity(combination.getId());
            for (Subscription subscription : subscriptions) {
                this.marketDataService.unsubscribe(subscription.getStrategy().getName(), subscription.getSecurity().getId());
            }

            // update the ComponentWindow
            for (Component component : combination.getComponents()) {

                // update the ComponentWindow
                removeFromComponentWindow(component);
            }

            // remove the combination
            this.combinationDao.delete(combination);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleted combination {}", combination);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Combination addComponentQuantity(final long combinationId, final long securityId, final long quantity) {

        return addOrRemoveComponentQuantity(combinationId, securityId, quantity, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Combination setComponentQuantity(final long combinationId, final long securityId, final long quantity) {

        return addOrRemoveComponentQuantity(combinationId, securityId, quantity, false);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Combination removeComponent(final long combinationId, final long securityId) {

        Combination combination = this.combinationDao.get(combinationId);

        if (combination == null) {
            throw new IllegalArgumentException("combination does not exist: " + combinationId);
        }

        String combinationString = combination.toString();

        if (this.securityDao.load(securityId) == null) {
            throw new IllegalArgumentException("security does not exist: " + securityId);
        }

        // find the component to the specified security
        Component component = CollectionUtils.find(combination.getComponents(), new Predicate<Component>() {
            @Override
            public boolean evaluate(Component component) {
                return component.getSecurity().getId() == securityId;
            }
        });

        if (component != null) {

            // update the combination
            combination.getComponents().remove(component);

            // delete the component
            this.componentDao.delete(component);

            // remove the component from the ComponentWindow
            removeFromComponentWindow(component);

            // update the ComponentWindow
            insertIntoComponentWindow(combination);

        } else {

            throw new IllegalArgumentException("component on securityId " + securityId + " does not exist");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removed component {} from combination {}", component, combinationString);
        }

        return combination;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetComponentWindow() {

        // emtpy the entire component window
        removeFromComponentWindow(null);

        // reset the component window
        for (Combination combination : this.combinationDao.loadAll()) {
            insertIntoComponentWindow(combination);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        if (!this.commonConfig.isSimulation()) {
            for (Combination combination : this.combinationDao.loadAll()) {
                insertIntoComponentWindow(combination);
            }
        }

    }

    private Combination addOrRemoveComponentQuantity(long combinationId, final long securityId, long quantity, boolean add) {

        Combination combination = this.combinationDao.get(combinationId);

        if (combination == null) {
            throw new IllegalArgumentException("combination does not exist: " + combinationId);
        }

        final Security security = this.securityDao.load(securityId);

        if (security == null) {
            throw new IllegalArgumentException("security does not exist: " + securityId);
        }

        // find the component to the specified security
        Component component = CollectionUtils.find(combination.getComponents(), new Predicate<Component>() {
            @Override
            public boolean evaluate(Component component) {
                return security.getId() == component.getSecurity().getId();
            }
        });

        if (component != null) {

            // add or set the quantity
            if (add) {
                if (quantity == 0) {
                    return combination;
                } else {
                    component.setQuantity(component.getQuantity() + quantity);
                }
            } else {
                if (component.getQuantity() == quantity) {
                    return combination;
                } else {
                    component.setQuantity(quantity);
                }
            }

        } else {

            // create a new component
            component = Component.Factory.newInstance();
            component.setSecurity(security);
            component.setQuantity(quantity);

            // associate combination
            component.setCombination(combination);

            this.componentDao.save(component);

            // reverse associate combination (after component has received an id)
            combination.getComponents().add(component);
        }

        // update the ComponentWindow
        insertIntoComponentWindow(combination);

        if (LOGGER.isDebugEnabled()) {
            if (add) {
                LOGGER.debug("added component quantity {} of {} to combination {}", quantity, component, combination.getId());
            } else {
                LOGGER.debug("set component quantity {} of {} to combination {}", quantity, component, combination.getId());
            }
        }

        return combination;
    }

    /**
     * send a RemoveComponentEvent to remove one or all components from the component window
     */
    private void removeFromComponentWindow(Component component) {

        // if a component is specified remove it, otherwise empty the entire window
        if (component != null) {
            this.serverEngine.executeQuery("delete from ComponentWindow where componentId = " + component.getId());
        } else {
            this.serverEngine.executeQuery("delete from ComponentWindow");
        }
    }

    /**
     * reset all entries in the ComponentWindow as parentSecurity.componentCount might have changed
     */
    private void insertIntoComponentWindow(Combination combination) {

        for (Component component : combination.getComponents()) {

            InsertComponentEventVO insertComponentEvent = new InsertComponentEventVO();
            insertComponentEvent.setComponentId(component.getId());
            insertComponentEvent.setQuantity(component.getQuantity());
            insertComponentEvent.setSecurityId(component.getSecurity().getId());
            insertComponentEvent.setCombinationId(combination.getId());
            insertComponentEvent.setComponentCount(combination.getComponentCount());

            this.serverEngine.sendEvent(insertComponentEvent);
        }
    }
}
