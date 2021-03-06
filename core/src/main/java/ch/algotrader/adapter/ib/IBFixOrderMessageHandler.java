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
package ch.algotrader.adapter.ib;

import java.util.concurrent.BlockingQueue;

import ch.algotrader.adapter.fix.fix42.GenericFix42OrderMessageHandler;
import ch.algotrader.service.OrderExecutionService;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.field.ExecType;
import quickfix.field.FAConfigurationAction;
import quickfix.field.FARequestID;
import quickfix.field.OrdStatus;
import quickfix.field.XMLContent;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.IBFAModification;

/**
 * IB specific Fix42MessageHandler.
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 */
public class IBFixOrderMessageHandler extends GenericFix42OrderMessageHandler {

    private final BlockingQueue<IBCustomMessage> allocationMessageQueue;

    public IBFixOrderMessageHandler(
            final OrderExecutionService orderExecutionService,
            final BlockingQueue<IBCustomMessage> allocationMessageQueue) {
        super(orderExecutionService);
        this.allocationMessageQueue = allocationMessageQueue;
    }

    @Override
    public void onMessage(ExecutionReport executionReport, SessionID sessionID) throws FieldNotFound {

        // ignore FA transfer execution reports
        if (executionReport.getExecID().getValue().startsWith("F-") || executionReport.getExecID().getValue().startsWith("U+")) {
            return;
        }

        // ignore FA ExecType=NEW / OrdStatus=FILLED (since they arrive after ExecType=FILL)
        if (executionReport.getExecType().getValue() == ExecType.NEW && executionReport.getOrdStatus().getValue() == OrdStatus.FILLED) {
            return;
        }

        super.onMessage(executionReport, sessionID);
    }

    /**
     * updates IB Group definitions
     */
    public void onMessage(IBFAModification faModification, SessionID sessionID) throws FieldNotFound {

        String fARequestID = faModification.get(new FARequestID()).getValue();
        String xmlContent = faModification.get(new XMLContent()).getValue();
        FAConfigurationAction fAConfigurationAction = faModification.get(new FAConfigurationAction());

        if (fAConfigurationAction.valueEquals(FAConfigurationAction.GET_GROUPS)) {
            IBCustomMessage message = new IBCustomMessage(fARequestID, IBCustomMessage.Type.GET_GROUPS, xmlContent);
            this.allocationMessageQueue.add(message);
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
