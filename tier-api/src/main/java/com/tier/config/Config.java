package com.tier.config;

public class Config {

    public static String env(final String environmentVariableName) {
        return env(environmentVariableName, null);
    }

    public static String env(final String environmentVariableName, final String defaultValue) {
        final String var = System.getenv(environmentVariableName);
        if (var == null || var.trim().isEmpty()) {
            return defaultValue;
        }
        return var;
    }

}
