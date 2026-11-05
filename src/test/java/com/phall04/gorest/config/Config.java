package com.phall04.gorest.config;

import io.github.cdimascio.dotenv.Dotenv;

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

    public static String token() {
        String token = System.getenv("GOREST_TOKEN");

        if (token == null || token.isBlank()) {
            token = Dotenv.configure().ignoreIfMissing().load().get("GOREST_TOKEN");
        }

        if (token == null || token.isBlank()) {
            throw new IllegalStateException("GOREST_TOKEN is not set");
        }

        return token;
    }
}