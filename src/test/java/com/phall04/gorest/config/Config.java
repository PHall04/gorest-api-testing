package com.phall04.gorest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("apiconfig.properties")) {
            if (input == null) {
                throw new IllegalStateException("apiconfig.properties not found");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config", e);
        }
    }

    private Config() {}

    public static String baseUrl() {
        return PROPERTIES.getProperty("base.url");
    }
}