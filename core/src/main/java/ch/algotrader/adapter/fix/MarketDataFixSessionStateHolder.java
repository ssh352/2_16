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
package ch.algotrader.adapter.fix;

import ch.algotrader.enumeration.FeedType;
import ch.algotrader.event.dispatch.EventDispatcher;

/**
 * Market Data feed specific {@link FixSessionStateHolder}
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @version $Revision: 6424 $ $Date: 2013-11-06 14:29:48 +0100 (Mi, 06 Nov 2013) $
 */
public class MarketDataFixSessionStateHolder extends DefaultFixSessionStateHolder {

    private final FeedType feedType;

    public MarketDataFixSessionStateHolder(final String name, final EventDispatcher eventDispatcher, final FeedType feedType) {
        super(name, eventDispatcher);
        this.feedType = feedType;
    }

    public FeedType getFeedType() {
        return this.feedType;
    }

}
