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
package ch.algotrader.esper.io;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import ch.algotrader.ServiceLocator;
import ch.algotrader.entity.marketData.Bar;
import ch.algotrader.enumeration.Duration;
import ch.algotrader.service.ServerLookupService;

import com.espertech.esper.adapter.AdapterState;
import com.espertech.esper.client.EPException;
import com.espertech.esperio.AbstractCoordinatedAdapter;
import com.espertech.esperio.SendableEvent;

/**
 * A {@link com.espertech.esperio.CoordinatedAdapter} used to input {@link ch.algotrader.entity.marketData.Bar Bars}
 * for all subscribed secruitiesin 1-day batches.
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 */
public class DBBarInputAdapter extends AbstractCoordinatedAdapter {


    private Iterator<Bar> iterator = (new ArrayList<Bar>()).iterator();
    private Date startDate;
    private final int batchSize;
    private final Duration barSize;
    private final ServerLookupService serverLookupService;

    public DBBarInputAdapter(int batchSize, Duration barSize) {
        super(null, true, true, true);
        this.batchSize = batchSize;
        this.barSize = barSize;

        this.serverLookupService = ServiceLocator.instance().getService("serverLookupService", ServerLookupService.class);
        Bar bar = this.serverLookupService.getFirstSubscribedBarByBarSize(this.barSize);
        this.startDate = bar.getDateTime();
    }

    @Override
    protected void close() {
        //do nothing
    }

    @Override
    protected void replaceFirstEventToSend() {

        this.eventsToSend.remove(this.eventsToSend.first());
        SendableEvent event = read();
        if (event != null) {
            this.eventsToSend.add(event);
        }
    }

    @Override
    protected void reset() {
        // do nothing
    }

    @Override
    public SendableEvent read() throws EPException {

        if (this.stateManager.getState() == AdapterState.DESTROYED) {
            return null;
        }

        if (this.eventsToSend.isEmpty()) {

            // get the next batch
            if (!this.iterator.hasNext()) {
                Date endDate = DateUtils.addDays(this.startDate, this.batchSize);

                List<Bar> bars = this.serverLookupService.getSubscribedBarsByTimePeriodAndBarSize(this.startDate, endDate, this.barSize);

                if (bars.size() > 0) {
                    this.iterator = bars.iterator();
                }

                this.startDate = endDate;
            }

            if (this.iterator.hasNext()) {
                Bar bar = this.iterator.next();
                return new SendableBaseObjectEvent(bar, bar.getDateTime().getTime(), this.scheduleSlot);
            } else {
                if (this.stateManager.getState() == AdapterState.STARTED) {
                    stop();
                } else {
                    destroy();
                }
                return null;
            }
        } else {

            SendableEvent event = this.eventsToSend.first();
            this.eventsToSend.remove(event);
            return event;
        }
    }
}
