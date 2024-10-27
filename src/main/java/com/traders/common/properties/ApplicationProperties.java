package com.traders.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to auth_Service.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();


    public Liquibase getLiquibase() {
        return liquibase;
    }


    public static class Liquibase {

        private Boolean asyncStart;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }
}
