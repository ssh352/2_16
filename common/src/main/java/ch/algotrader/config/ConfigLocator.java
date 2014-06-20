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
package ch.algotrader.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.algotrader.config.spring.DefaultConfigLoader;
import ch.algotrader.config.spring.DefaultSystemConfigProvider;
import ch.algotrader.util.MyLogger;

/**
 * Configuration parameter and base configuration locator.
 *
 * @author <a href="mailto:okalnichevski@algotrader.ch">Oleg Kalnichevski</a>
 *
 * @version $Revision$ $Date$
 */
public final class ConfigLocator {

    private static final Logger LOGGER = MyLogger.getLogger(ConfigLocator.class.getName());

    private static ConfigLocator INSTANCE;

    private final ConfigParams configParams;
    private final BaseConfig baseConfig;
    private final Map<Class<?>, Object> beanMap;

    private ConfigLocator(final ConfigParams configParams, final BaseConfig baseConfig) {
        this.configParams = configParams;
        this.baseConfig = baseConfig;
        this.beanMap = new HashMap<Class<?>, Object>();
        this.beanMap.put(BaseConfig.class, this.baseConfig);
    }

    public ConfigParams getConfigParams() {
        return this.configParams;
    }

    public BaseConfig getBaseConfig() {
        return this.baseConfig;
    }

    public <T> T getConfig(final Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        synchronized (this.beanMap) {
            T bean = clazz.cast(this.beanMap.get(clazz));
            if (bean == null) {
                bean = new ConfigBeanFactory().create(this.configParams, clazz);
                this.beanMap.put(clazz, bean);
            }
            return bean;
        }
    }

    public synchronized static void initialize(final ConfigParams configParams, final BaseConfig baseConfig) {

        Validate.notNull(configParams, "ConfigParams is null");
        Validate.notNull(baseConfig, "ATConfig is null");
        INSTANCE = new ConfigLocator(configParams, baseConfig);
    }

    public synchronized static ConfigLocator instance() {

        if (INSTANCE == null) {
            try {

                INSTANCE = standaloneInit();
            } catch (Exception ex) {

                LOGGER.error("Unexpected I/O error reading configuration", ex);
                INSTANCE = new ConfigLocator(new ConfigParams(new NoOpConfigProvider()), BaseConfigBuilder.create().build());
            }
        }
        return INSTANCE;
    }

    private static class NoOpConfigProvider implements ConfigProvider {

        @Override
        public <T> T getParameter(String name, Class<T> clazz) {
            return null;
        }

    }

    private static ConfigLocator standaloneInit() throws Exception {
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver(ConfigLocator.class.getClassLoader());
        DefaultConfigLoader loader = new DefaultConfigLoader(patternResolver);
        DefaultSystemConfigProvider configProvider = new DefaultSystemConfigProvider(loader.getParams(), new DefaultConversionService());
        ConfigParams configParams = new ConfigParams(configProvider);
        BaseConfig baseConfig = new ConfigBeanFactory().create(configParams, BaseConfig.class);
        return new ConfigLocator(configParams, baseConfig);
    }

}