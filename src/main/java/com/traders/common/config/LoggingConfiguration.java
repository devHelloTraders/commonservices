package com.traders.common.config;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traders.common.appconfig.logging.LoggingUtils;
import com.traders.common.properties.ConfigProperties;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/*
 * Configures the console and Logstash log appenders from the app properties
 */
@Configuration
@RefreshScope
public class LoggingConfiguration {

    public LoggingConfiguration(
        @Value("${spring.application.name}") String appName,
        @Value("${server.port}") String serverPort,
        ConfigProperties configProperties,
        ObjectProvider<ConsulRegistration> consulRegistration,
        ObjectProvider<BuildProperties> buildProperties,
        ObjectMapper mapper
    ) throws JsonProcessingException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Map<String, String> map = new HashMap<>();
        map.put("app_name", appName);
        map.put("app_port", serverPort);
        buildProperties.ifAvailable(it -> map.put("version", it.getVersion()));
        consulRegistration.ifAvailable(it -> map.put("instance_id", it.getInstanceId()));
        String customFields = mapper.writeValueAsString(map);

        ConfigProperties.Logging loggingProperties = configProperties.getLogging();
        ConfigProperties.Logging.Logstash logstashProperties = loggingProperties.getLogstash();

        if (loggingProperties.isUseJsonFormat()) {
            LoggingUtils.addJsonConsoleAppender(context, customFields);
        }
        if (logstashProperties.isEnabled()) {
            LoggingUtils.addLogstashTcpSocketAppender(context, customFields, logstashProperties);
        }
        if (loggingProperties.isUseJsonFormat() || logstashProperties.isEnabled()) {
            LoggingUtils.addContextListener(context, customFields, loggingProperties);
        }
    }
}
