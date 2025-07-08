package com.ambient.performance.simulations;

import com.ambient.performance.base.BaseSimulation;
import com.ambient.performance.model.AppointmentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Performance simulation that dynamically creates appointments using data from a JSON feeder.
 *
 * Key features:
 * - Loads appointment data from `appointments.json`.
 * - Dynamically constructs request bodies using `AppointmentRequest`.
 * - Ramps and sustains user load based on config values.
 * - Includes performance assertions for mean response time and success rate.
 */
public class CreateAppointmentsSimulation extends BaseSimulation {

    /**
     * Feeder initialized from `resources/data/appointments.json`.
     * Provides randomized appointment data for each virtual user.
     */
    private final FeederBuilder<Object> appointmentFeeder = loadFeeder();

    /**
     * Loads appointment data from the JSON file and converts it to a Gatling feeder.
     * Uses Jackson for parsing and wraps it with a `random()` strategy.
     *
     * @return a randomized feeder with appointment test data
     */
    private FeederBuilder<Object> loadFeeder() {
        try {
            // Load the JSON resource file from classpath
            InputStream input = Objects.requireNonNull(
                    getClass().getResourceAsStream("/data/appointments.json"),
                    "Could not find feeder data file"
            );

            // Parse JSON into list of maps (each map is one appointment)
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> appointments = mapper.readValue(input, List.class);

            // Return randomized feeder for varied simulation data
            return listFeeder(appointments).random();
        } catch (Exception e) {
            throw new RuntimeException("Could not load feeder data", e);
        }
    }

    /**
     * Defines the appointment creation scenario:
     * - Feeds dynamic data from the JSON file.
     * - Constructs the POST request payload using session variables.
     * - Verifies response status and response time.
     */
    ScenarioBuilder createAppointments = scenario("Create Appointments Dynamic")
            .feed(appointmentFeeder) // Load session variables from the feeder
            .exec(
                    http("Create Appointment")
                            .post("/v1/appointments/create")
                            .body(StringBody(session -> {
                                try {
                                    // Build the request object using session data
                                    AppointmentRequest req = new AppointmentRequest();
                                    req.setClient_id(session.getString("client_id"));

                                    AppointmentRequest.Item item = new AppointmentRequest.Item();
                                    item.setScheduled_start(session.getString("item.scheduled_start"));
                                    item.setScheduled_end(session.getString("item.scheduled_end"));
                                    item.setPatient_name(session.getString("item.patient_name"));
                                    item.setNotes(session.getString("item.notes"));

                                    req.setItem(item);

                                    // Serialize the request to JSON
                                    return new ObjectMapper().writeValueAsString(req);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException("Failed to serialize appointment request", e);
                                }
                            }))
                            .check(status().in(200, 201))               // Accept success or created responses
                            .check(responseTimeInMillis().lt(3000))     // Ensure fast response
            )
            .pause(Duration.ofSeconds(1));                             // Simulate user think time

    {
        /**
         * Sets up the simulation with:
         * - Ramp-up phase from 1 user/sec to configured user count.
         * - Constant load phase at target user rate.
         * Applies the shared HTTP protocol configuration.
         * Adds assertions on performance thresholds.
         */
        setUp(
                createAppointments.injectOpen(
                        rampUsersPerSec(1).to(config.getInt("usersCreate"))
                                .during(Duration.ofSeconds(config.getInt("rampDuration"))),
                        constantUsersPerSec(config.getInt("usersCreate"))
                                .during(Duration.ofSeconds(config.getInt("constantDuration")))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().mean().lt(2000),              // Ensure average response < 2s
                global().successfulRequests().percent().gt(95.0)      // Ensure at least 95% success
        );
    }
}
