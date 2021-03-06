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

import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.algotrader.dao.HibernateInitializer;
import ch.algotrader.entity.BaseEntityI;

/**
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 */
@Transactional(propagation = Propagation.SUPPORTS)
public class LazyLoaderServiceImpl implements LazyLoaderService {

    public LazyLoaderServiceImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntityI> T lazyLoadProxy(BaseEntityI entity, String context, T proxy) {

        Validate.notNull(entity, "Entity is null");
        Validate.notEmpty(context, "Context is empty");
        Validate.notNull(proxy, "Proxy is null");

        return HibernateInitializer.INSTANCE.initializeProxy(entity, context, proxy);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends BaseEntityI> Collection<T> lazyLoadCollection(BaseEntityI entity, String context, Collection<T> col) {

        Validate.notNull(entity, "Entity is null");
        Validate.notEmpty(context, "Context is empty");
        Validate.notNull(col, "Col is null");

        return HibernateInitializer.INSTANCE.initializeCollection(entity, context, col);

    }

}
