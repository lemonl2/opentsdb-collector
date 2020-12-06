package com.eoi.collector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author lemon
 */
@Configuration
public class WebCustom implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Value("${collector.port}")
    private Integer port;

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(port);
    }
}

