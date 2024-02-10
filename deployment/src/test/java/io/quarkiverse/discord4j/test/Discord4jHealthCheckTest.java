package io.quarkiverse.discord4j.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.enterprise.inject.Any;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.discord4j.runtime.health.Discord4jHealthCheck;
import io.quarkus.test.QuarkusUnitTest;

public class Discord4jHealthCheckTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application.properties");

    @Any
    Discord4jHealthCheck health;

    @Test
    public void testHealthCheck() {
        HealthCheckResponse response = health.call().await().indefinitely();
        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
        assertEquals(Discord4jHealthCheck.NAME, response.getName());
    }
}
