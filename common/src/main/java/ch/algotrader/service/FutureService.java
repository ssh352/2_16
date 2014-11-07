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

import java.util.Date;
import java.util.List;

import ch.algotrader.entity.security.Future;

/**
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @version $Revision$ $Date$
 */
public interface FutureService {

    /**
     * Creates the missing Part of the Futures Chain defined by the {@code futureFamilyId}.
     */
    public void createDummyFutures(int futureFamilyId);

    /**
     * Get the first Future with {@code expirationDate} after the specified {@code
     * targetExpirationDate} and {@code futureFamilyId}.
     * In simulation mode, if {@code simulateFuturesByUnderlying} or {code
     * simulateFuturesByGenericFutures} is configured, a dummy Future will be created if none was
     * found.
     */
    public Future getFutureByMinExpiration(int futureFamilyId, Date targetExpirationDate);

    /**
     * Gets a Future by its {@link ch.algotrader.entity.security.FutureFamily} and {@code expirationDate}.
     * In simulation mode, if {@code simulateFuturesByUnderlying} or {code
     * simulateFuturesByGenericFutures} is configured, a dummy Future will be created if none was
     * found.
     */
    public Future getFutureByExpiration(int futureFamilyId, Date expirationDate);

    /**
     * Gets a Future by its {@link ch.algotrader.entity.security.FutureFamily}, that is {@code n} durations after the specified
     * {@code targetExpirationDate}.
     * In simulation mode, if {@code simulateFuturesByUnderlying} or {code
     * simulateFuturesByGenericFutures} is configured, a dummy Future will be created if none was
     * found.
     */
    public Future getFutureByDuration(int futureFamilyId, Date targetDate, int duration);

    /**
     * Gets all Futures with {@code expirationDate} after the specified {@code targetExpirationDate}
     * and {@code futureFamilyId}. The returned Futures are sorted with their {@code expirationDate}
     * in ascending order.
     */
    public List<Future> getFuturesByMinExpiration(int futureFamilyId, Date minExpirationDate);

}