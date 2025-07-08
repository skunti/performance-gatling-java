package com.ambient.performance.simulations;

import com.ambient.performance.base.BaseSimulation;
import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;

/**
 * Performance simulation for statically creating appointments via the `/v1/appointments/create` endpoint.
 *
 * This simulation:
 * - Sends POST requests using a static JSON payload.
 * - Ramps up and maintains a load of users based on config.
 * - Asserts that requests return success codes (200 or 201) and meet timing thresholds.
 *
 * Configuration values (from `config.properties`):
 * - `usersCreateStatic`: number of virtual users.
 * - `rampDuration`: duration in seconds to ramp up the users.
 * - `constantDuration`: duration in seconds for constant load phase (divided by 2 here).
 */
public class CreateAppointmentsStaticSimulation extends BaseSimulation {

    /**
     * Scenario that sends a static POST request to create a new appointment.
     * - Uses request body from `data/create_appointment.json`.
     * - Verifies the status is 200 or 201.
     * - Checks that response time is under 3000 milliseconds.
     */
    ScenarioBuilder createStatic = scenario("Create Appointments Static")
            .exec(
                    http("Create Appointment Static")
                            .post("/v1/appointments/create")
                            .body(RawFileBody("data/create_appointment.json")) // Static payload file
                            .check(status().in(200, 201))                     // Accept 200 OK or 201 Created
                            .check(responseTimeInMillis().lt(3000))           // Ensure quick response
            )
            .pause(Duration.ofSeconds(1));                                    // Simulated user think time

    {
        /**
         * Set up the scenario execution with:
         * - Ramp-up phase: increase users from 1 to `usersCreateStatic` over `rampDuration`.
         * - Constant phase: maintain `usersCreateStatic` users for half of `constantDuration`.
         * Applies common HTTP protocol config from BaseSimulation.
         */
        setUp(
                createStatic.injectOpen(
                        rampUsersPerSec(1).to(config.getInt("usersCreateStatic"))
                                .during(Duration.ofSeconds(config.getInt("rampDuration"))),
                        constantUsersPerSec(config.getInt("usersCreateStatic"))
                                .during(Duration.ofSeconds(config.getInt("constantDuration") / 2))
                ).protocols(httpProtocol)
        );
    }
}
