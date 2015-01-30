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
package ch.algotrader.enumeration;

/**
 * {@code PUT} or {@code CALL}
 */
public enum InitializingServiceType {

    CORE(0), BROKER_INTERFACE(1);

    private static final long serialVersionUID = -7991848187098285788L;

    private final int enumValue;

    /**
     * The constructor with enumeration literal value allowing
     * super classes to access it.
     */
    private InitializingServiceType(int value) {

        this.enumValue = value;
    }

    /**
     * Gets the underlying value of this type safe enumeration.
     * This method is necessary to comply with DaoBase implementation.
     * @return The name of this literal.
     */
    public int getValue() {

        return this.enumValue;
    }

}
