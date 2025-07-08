package com.ambient.performance.base;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * BaseSimulation provides common configuration for all Gatling simulations in the project.
 *
 * This class:
 * - Loads application-specific settings from a `config.properties` file.
 * - Defines a shared HTTP protocol configuration with base URL and content type.
 *
 * All simulation classes should extend this class to avoid duplication.
 */
public abstract class BaseSimulation extends Simulation {

    /**
     * Loads configuration values from `resources/config.properties` using Typesafe Config.
     * This allows central control of variables like base URL, user load, and durations.
     */
    protected Config config = ConfigFactory.load("config.properties");

    /**
     * Base URL for all HTTP requests, configured via `baseUrl` in the properties file.
     */
    protected String baseUrl = config.getString("baseUrl");

    /**
     * Common HTTP protocol settings used in all scenarios:
     * - Sets the base URL for the API.
     * - Specifies `application/json` as the default content type header.
     */
    protected HttpProtocolBuilder httpProtocol = http
            .baseUrl(baseUrl)
            .contentTypeHeader("application/json");
}
