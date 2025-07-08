package com.ambient.performance.config;

/**
 * GatlingConfig provides environment-based configuration values for simulations.
 *
 * This utility class allows simulations to dynamically read values from environment variables,
 * falling back to defaults when not provided. This is useful for parameterizing tests in CI/CD pipelines.
 */
public class GatlingConfig {

    /**
     * Returns the base URL for the system under test.
     *
     * @return the value of the BASE_URL environment variable, or "http://localhost:3000" if not set
     */
    public static String baseUrl() {
        return System.getenv().getOrDefault("BASE_URL", "http://localhost:3000");
    }

    /**
     * Returns the number of virtual users to simulate.
     *
     * @return the integer value of the USER_COUNT environment variable, or 10 if not set
     * @throws NumberFormatException if USER_COUNT is set to a non-integer value
     */
    public static int userCount() {
        return Integer.parseInt(System.getenv().getOrDefault("USER_COUNT", "10"));
    }
}
