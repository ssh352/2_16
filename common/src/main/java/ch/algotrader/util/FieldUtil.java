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
package ch.algotrader.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides general Class related untility Methods.
 *
 * @author <a href="mailto:aflury@algotrader.ch">Andy Flury</a>
 *
 * @see BeanUtil
 */
public class FieldUtil {

    private static final Logger LOGGER = LogManager.getLogger(FieldUtil.class);

    private static final Set<Object> immediates =
        new HashSet<>(Arrays.asList(new Object[]{

                    Boolean.class,
                    Double.class,
                    Float.class,
                    Integer.class,
                    Long.class,
                    Short.class,
                    Character.class,
                    Byte.class,

                    Boolean.TYPE,
                    Double.TYPE,
                    Float.TYPE,
                    Integer.TYPE,
                    Long.TYPE,
                    Short.TYPE,
                    Character.TYPE,
                    Byte.TYPE,

                    String.class,
                    BigDecimal.class,
                    Date.class,
                    Class.class}));

    /**
     * returns true if the {@code field} is a primitive, a primitive wrapper, String, BigDecimal, Date or Class.
     */
    public static boolean isSimpleAttribute(Field field) {
        return immediates.contains(field.getType()) || Enum.class.isAssignableFrom(field.getType());
    }

    /**
     * Returns all non-static / non-transient Fields including Fields defined by superclasses of the defined {@code type}
     */
    public static List<Field> getAllFields(Class<?> type) {

        List<Field> fields = new ArrayList<>();

        while (true) {

            for (Field field : type.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                    setAccessible(field);
                    fields.add(field);
                }
            }

            type = type.getSuperclass();
            if (type == Object.class || type == null) {
                break;
            }
        }

        return fields;
    }

    /**
     * copies all field values by direct field access.
     */
    public static void copyAllFields(Object source, Object target) {

        for (Field field : FieldUtil.getAllFields(target.getClass())) {

            try {
                Object targetValue = FieldUtils.readField(field, source, true);
                FieldUtils.writeField(field, target, targetValue, true);
            } catch (IllegalAccessException e) {
                LOGGER.error("problem copying field", e);
            }
        }
    }

    private static void setAccessible(final AccessibleObject object) {

        if (object.isAccessible())
            return;

        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            object.setAccessible(true);
            return null;
        });
    }
}
