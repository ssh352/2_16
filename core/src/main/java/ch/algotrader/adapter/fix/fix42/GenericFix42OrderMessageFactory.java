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
package ch.algotrader.adapter.fix.fix42;

import java.util.Date;

import org.apache.commons.lang.Validate;

import ch.algotrader.adapter.fix.FixUtil;
import ch.algotrader.entity.Account;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.trade.LimitOrderI;
import ch.algotrader.entity.trade.SimpleOrder;
import ch.algotrader.entity.trade.StopOrderI;
import ch.algotrader.enumeration.TIF;
import ch.algotrader.util.PriceUtil;
import quickfix.field.ClOrdID;
import quickfix.field.ExDestination;
import quickfix.field.ExpireTime;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.StopPx;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelRequest;

/**
 * Generic FIX/4.2 order message factory implementation.
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 */
public class GenericFix42OrderMessageFactory implements Fix42OrderMessageFactory {

    private final Fix42SymbologyResolver symbologyResolver;

    public GenericFix42OrderMessageFactory(final Fix42SymbologyResolver symbologyResolver) {
        Validate.notNull(symbologyResolver, "FIX symbology resolver is null");
        this.symbologyResolver = symbologyResolver;
    }

    protected TimeInForce resolveTimeInForce(final TIF tif) {
        return FixUtil.getTimeInForce(tif);
    }

    @Override
    public NewOrderSingle createNewOrderMessage(final SimpleOrder order, final String clOrdID) {

        NewOrderSingle message = new NewOrderSingle();
        Security security = order.getSecurity();
        Account account = order.getAccount();
        String broker = account.getBroker();

        // common info
        message.set(new ClOrdID(clOrdID));
        message.set(new TransactTime(new Date()));

        this.symbologyResolver.resolve(message, security, broker);

        message.set(FixUtil.getFixSide(order.getSide()));
        message.set(new OrderQty(order.getQuantity()));
        message.set(FixUtil.getFixOrderType(order));

        // exchange
        String exchangeCode = null;
        if (order.getExchange() != null) {
            exchangeCode = order.getExchange().getCode();
        } else if (security.getSecurityFamily().getExchange() != null) {
            exchangeCode = security.getSecurityFamily().getExchange().getCode();
        }

        if (exchangeCode != null) {
            message.set(new ExDestination(exchangeCode));
        }

        // account
        if (account.getExtAccount() != null) {
            message.set(new quickfix.field.Account(account.getExtAccount()));
        }

        // set the limit price if order is a limit order or stop limit order
        if (order instanceof LimitOrderI) {
            LimitOrderI limitOrder = (LimitOrderI) order;
            message.set(new Price(PriceUtil.denormalizePrice(order, limitOrder.getLimit())));
        }

        // set the stop price if order is a stop order or stop limit order
        if (order instanceof StopOrderI) {
            StopOrderI stopOrder = (StopOrderI) order;
            message.set(new StopPx(PriceUtil.denormalizePrice(order, stopOrder.getStop())));
        }

        // set TIF
        if (order.getTif() != null) {
            message.set(resolveTimeInForce(order.getTif()));
            if (order.getTif() == TIF.GTD && order.getTifDateTime() != null) {
                message.set(new ExpireTime(order.getTifDateTime()));
            }
        }

        FixUtil.copyOrderProperties(message, order.getOrderProperties());

        return message;
    }

    @Override
    public OrderCancelReplaceRequest createModifyOrderMessage(final SimpleOrder order, final String clOrdID) {

        String origClOrdID = order.getIntId();

        OrderCancelReplaceRequest message = new OrderCancelReplaceRequest();
        Security security = order.getSecurity();
        Account account = order.getAccount();
        String broker = account.getBroker();

        // common info
        message.set(new ClOrdID(clOrdID));
        message.set(new OrigClOrdID(origClOrdID));
        message.set(new TransactTime(new Date()));

        this.symbologyResolver.resolve(message, security, broker);

        message.set(FixUtil.getFixSide(order.getSide()));
        message.set(new OrderQty(order.getQuantity()));
        message.set(FixUtil.getFixOrderType(order));

        // handling for accounts
        if (account.getExtAccount() != null) {
            message.set(new quickfix.field.Account(account.getExtAccount()));
        }

        //set the limit price if order is a limit order or stop limit order
        if (order instanceof LimitOrderI) {
            LimitOrderI limitOrder = (LimitOrderI) order;
            message.set(new Price(PriceUtil.denormalizePrice(order, limitOrder.getLimit())));
        }

        //set the stop price if order is a stop order or stop limit order
        if (order instanceof StopOrderI) {
            StopOrderI stopOrder = (StopOrderI) order;
            message.set(new StopPx(PriceUtil.denormalizePrice(order, stopOrder.getStop())));
        }

        // set TIF
        if (order.getTif() != null) {
            message.set(resolveTimeInForce(order.getTif()));
            if (order.getTif() == TIF.GTD && order.getTifDateTime() != null) {
                message.set(new ExpireTime(order.getTifDateTime()));
            }
        }

        FixUtil.copyOrderProperties(message, order.getOrderProperties());

        return message;
    }

    @Override
    public OrderCancelRequest createOrderCancelMessage(final SimpleOrder order, final String clOrdID) {

        String origClOrdID = order.getIntId();

        OrderCancelRequest message = new OrderCancelRequest();
        Security security = order.getSecurity();
        Account account = order.getAccount();
        String broker = account.getBroker();

        // common info
        message.set(new ClOrdID(clOrdID));
        message.set(new OrigClOrdID(origClOrdID));
        message.set(new TransactTime(new Date()));

        this.symbologyResolver.resolve(message, security, broker);

        message.set(FixUtil.getFixSide(order.getSide()));
        message.set(new OrderQty(order.getQuantity()));

        // handling for accounts
        if (account.getExtAccount() != null) {
            message.set(new quickfix.field.Account(account.getExtAccount()));
        }

        return message;
    }

}
