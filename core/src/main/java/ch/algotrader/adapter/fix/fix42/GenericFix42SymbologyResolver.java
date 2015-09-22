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
package ch.algotrader.adapter.fix.fix42;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import ch.algotrader.adapter.fix.FixApplicationException;
import ch.algotrader.adapter.fix.FixUtil;
import ch.algotrader.entity.security.Forex;
import ch.algotrader.entity.security.Future;
import ch.algotrader.entity.security.Option;
import ch.algotrader.entity.security.OptionFamily;
import ch.algotrader.entity.security.Security;
import ch.algotrader.entity.security.SecurityFamily;
import ch.algotrader.entity.security.Stock;
import ch.algotrader.enumeration.OptionType;
import ch.algotrader.util.DateTimeLegacy;
import quickfix.field.ContractMultiplier;
import quickfix.field.Currency;
import quickfix.field.MaturityDay;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelRequest;

/**
 * Generic FIX/4.2 symbology resolver implementation.
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @version $Revision$ $Date$
 */
public class GenericFix42SymbologyResolver implements Fix42SymbologyResolver {

    private final DateTimeFormatter monthFormat;
    private final DateTimeFormatter dayFormat;

    public GenericFix42SymbologyResolver() {
        this.monthFormat = DateTimeFormatter.ofPattern("yyyyMM");
        this.dayFormat = DateTimeFormatter.ofPattern("dd");
    }

    protected String formatYM(final Date date) {
        if (date == null) {
            return null;
        }
        return this.monthFormat.format(DateTimeLegacy.toGMTDate(date));
    }

    protected String formatD(final Date date) {
        if (date == null) {
            return null;
        }
        return this.dayFormat.format(DateTimeLegacy.toGMTDate(date));
    }

    protected Symbol resolveSymbol(final Security security, final String broker) {

        return FixUtil.getFixSymbol(security, broker);
    }

    @Override
    public void resolve(final NewOrderSingle message, final Security security, final String broker) throws FixApplicationException {

        message.set(resolveSymbol(security, broker));

        SecurityFamily securityFamily = security.getSecurityFamily();

        // populate security information
        if (security instanceof Option) {

            Option option = (Option) security;
            OptionFamily optionFamily = (OptionFamily) securityFamily;

            message.set(new SecurityType(SecurityType.OPTION));
            message.set(new Currency(securityFamily.getCurrency().toString()));
            message.set(new ContractMultiplier(securityFamily.getContractSize(broker)));
            message.set(new PutOrCall(OptionType.PUT.equals(option.getType()) ? PutOrCall.PUT : PutOrCall.CALL));
            message.set(new StrikePrice(option.getStrike().doubleValue()));
            message.set(new MaturityMonthYear(formatYM(option.getExpiration())));

            if (optionFamily.isWeekly()) {

                message.set(new MaturityDay(formatD(option.getExpiration())));
            }

        } else if (security instanceof Future) {

            Future future = (Future) security;

            message.set(new SecurityType(SecurityType.FUTURE));
            message.set(new Currency(securityFamily.getCurrency().toString()));
            message.set(new ContractMultiplier(securityFamily.getContractSize(broker)));
            message.set(new MaturityMonthYear(formatYM(future.getExpiration())));

        } else if (security instanceof Forex) {

            message.set(new SecurityType(SecurityType.CASH));
            message.set(new Currency(securityFamily.getCurrency().name()));

        } else if (security instanceof Stock) {

            message.set(new SecurityType(SecurityType.COMMON_STOCK));
            message.set(new Currency(securityFamily.getCurrency().toString()));
        }

    }

    @Override
    public void resolve(final OrderCancelReplaceRequest message, final Security security, final String broker) throws FixApplicationException {

        message.set(resolveSymbol(security, broker));

        // populate security information
        if (security instanceof Option) {

            Option option = (Option) security;

            message.set(new SecurityType(SecurityType.OPTION));
            message.set(new PutOrCall(OptionType.PUT.equals(option.getType()) ? PutOrCall.PUT : PutOrCall.CALL));
            message.set(new StrikePrice(option.getStrike().doubleValue()));
            message.set(new MaturityMonthYear(formatYM(option.getExpiration())));

        } else if (security instanceof Future) {

            Future future = (Future) security;

            message.set(new SecurityType(SecurityType.FUTURE));
            message.set(new MaturityMonthYear(formatYM(future.getExpiration())));

        } else if (security instanceof Forex) {

            message.set(new SecurityType(SecurityType.CASH));

        } else if (security instanceof Stock) {
            message.set(new SecurityType(SecurityType.COMMON_STOCK));
        }
    }

    @Override
    public void resolve(final OrderCancelRequest message, final Security security, final String broker) throws FixApplicationException {

        message.set(resolveSymbol(security, broker));

        // populate security information
        if (security instanceof Option) {

            Option option = (Option) security;

            message.set(new SecurityType(SecurityType.OPTION));
            message.set(new PutOrCall(OptionType.PUT.equals(option.getType()) ? PutOrCall.PUT : PutOrCall.CALL));
            message.set(new StrikePrice(option.getStrike().doubleValue()));
            message.set(new MaturityMonthYear(formatYM(option.getExpiration())));

        } else if (security instanceof Future) {

            Future future = (Future) security;

            message.set(new SecurityType(SecurityType.FUTURE));
            message.set(new MaturityMonthYear(formatYM(future.getExpiration())));

        } else if (security instanceof Forex) {

            message.set(new SecurityType(SecurityType.CASH));

        } else if (security instanceof Stock) {
            message.set(new SecurityType(SecurityType.COMMON_STOCK));
        }
    }

}
