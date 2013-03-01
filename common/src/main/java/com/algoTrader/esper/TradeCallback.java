/***********************************************************************************
 * AlgoTrader Enterprise Trading Framework
 *
 * Copyright (C) 2013 Flury Trading - All rights reserved
 *
 * All information contained herein is, and remains the property of Flury Trading.
 * The intellectual and technical concepts contained herein are proprietary to
 * Flury Trading. Modification, translation, reverse engineering, decompilation,
 * disassembly or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from Flury Trading
 *
 * Fur detailed terms and conditions consult the file LICENSE.txt or contact
 *
 * Flury Trading
 * Badenerstrasse 16
 * 8004 Zurich
 ***********************************************************************************/
package com.algoTrader.esper;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.algoTrader.entity.trade.OrderStatus;
import com.algoTrader.util.MyLogger;
import com.algoTrader.util.metric.MetricsUtil;

/**
 * @author <a href="mailto:andyflury@gmail.com">Andy Flury</a>
 *
 * @version $Revision$ $Date$
 */
public abstract class TradeCallback {

    private static Logger logger = MyLogger.getLogger(TradeCallback.class.getName());

    private boolean expectFullExecution;

    public TradeCallback(boolean expectFullExecution) {

        this.expectFullExecution = expectFullExecution;
    }

    public void update(String strategyName, OrderStatus[] orderStati) throws Exception {

        // check full execution if needed
        if (this.expectFullExecution) {
            for (OrderStatus orderStatus : orderStati) {
                if (orderStatus.getRemainingQuantity() > 0) {
                    logger.error("order on " + orderStatus.getOrd().getSecurityInitialized() + " has not been fully executed, filledQty: " + orderStatus.getFilledQuantity() + " remainingQty: "
                            + orderStatus.getRemainingQuantity());
                }
            }
        }

        // get the securityIds sorted asscending
        List<OrderStatus> orderStatusList = Arrays.asList(orderStati);
        TreeSet<Integer> sortedSecurityIds = new TreeSet<Integer>(CollectionUtils.collect(orderStatusList, new Transformer<OrderStatus, Integer>() {
            @Override
            public Integer transform(OrderStatus order) {
                return order.getOrd().getSecurity().getId();
            }
        }));

        String owningStrategyName = orderStati[0].getOrd().getStrategy().getName();

        // get the statement alias based on all security ids
        String alias = "ON_TRADE_COMPLETED_" + StringUtils.join(sortedSecurityIds, "_") + "_" + owningStrategyName;

        // undeploy the statement
        EsperManager.undeployStatement(strategyName, alias);

        long startTime = System.nanoTime();
        logger.debug("onTradeCompleted start " + sortedSecurityIds + " " + owningStrategyName);

        // call orderCompleted
        onTradeCompleted(orderStatusList);

        logger.debug("onTradeCompleted end " + sortedSecurityIds + " " + owningStrategyName);

        MetricsUtil.accountEnd("TradeCallback." + strategyName, startTime);
    }

    public abstract void onTradeCompleted(List<OrderStatus> orderStatus) throws Exception;
}
