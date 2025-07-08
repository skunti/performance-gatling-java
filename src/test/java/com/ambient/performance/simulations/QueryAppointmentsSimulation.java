package com.ambient.performance.simulations;

import com.ambient.performance.base.BaseSimulation;
import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

/**
 * Performance simulation for the `/v1/appointments/query` endpoint.
 *
 * This simulation:
 * - Ramps up users over a configured duration.
 * - Sends a constant load for a configured duration.
 * - Verifies response status and timing for the appointment query endpoint.
 *
 * Configuration values (loaded from config.properties):
 * - `usersQuery`: number of virtual users.
 * - `rampDuration`: ramp-up time in seconds.
 * - `constantDuration`: steady-state load time in seconds.
 *
 * Assertions:
 * - 95% of requests must succeed.
 * - Maximum response time must be under 5000ms.
 */
public class QueryAppointmentsSimulation extends BaseSimulation {

    /**
     * Defines a Gatling scenario that sends GET requests to the `/v1/appointments/query` endpoint.
     * It checks for:
     * - HTTP 200 OK response.
     * - Response time under 2000ms.
     * Then pauses for 1 second before the next request.
     */
    ScenarioBuilder queryAppointments = scenario("Query Appointments")
            .exec(
                    http("Get Appointments")
                            .get("/v1/appointments/query")
                            .check(status().is(200))                     // Assert response status is 200
                            .check(responseTimeInMillis().lt(2000))     // Assert response time < 2000ms
            )
            .pause(Duration.ofSeconds(1));                              // Simulated user think time

    {
        /**
         * Executes the simulation with the configured load profile:
         * - Ramp up users from 1 to `usersQuery` over `rampDuration` seconds.
         * - Maintain constant user load for `constantDuration` seconds.
         * Applies HTTP protocol settings from BaseSimulation.
         *
         * Also applies global assertions to validate overall simulation health.
         */
        setUp(
                queryAppointments.injectOpen(
                        rampUsersPerSec(1).to(config.getInt("usersQuery"))       // Gradually increase load
                                .during(Duration.ofSeconds(config.getInt("rampDuration"))),
                        constantUsersPerSec(config.getInt("usersQuery"))         // Maintain load
                                .during(Duration.ofSeconds(config.getInt("constantDuration")))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().max().lt(5000),                          // Max response time < 5000ms
                global().successfulRequests().percent().gt(95.0)                // At least 95% success rate
        );
    }
}
