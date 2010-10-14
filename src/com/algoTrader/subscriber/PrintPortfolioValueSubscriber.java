package com.algoTrader.subscriber;

import org.apache.log4j.Logger;

import com.algoTrader.entity.Transaction;
import com.algoTrader.util.MyLogger;
import com.algoTrader.vo.PortfolioValueVO;

public class PrintPortfolioValueSubscriber {

    private static Logger logger = MyLogger.getLogger(PrintPortfolioValueSubscriber.class.getName());

    public void update(long timestamp, PortfolioValueVO portfolioValue, Transaction transaction) {

        logger.info(portfolioValue.getCashBalance() + "," + portfolioValue.getSecuritiesCurrentValue() + "," + portfolioValue.getMaintenanceMargin()
                + ((transaction != null) ? ("," + transaction.getValueDouble()) : ""));
    }
}
