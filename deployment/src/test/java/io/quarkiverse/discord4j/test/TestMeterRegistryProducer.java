package io.quarkiverse.discord4j.test;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Supplies a concrete {@link SimpleMeterRegistry} so the root {@code CompositeMeterRegistry} has a child registry
 * during tests. Without a child registry every gauge reads {@code 0} regardless of its backing value, because a
 * composite delegates reads to its children. Production deployments get a child from a real registry exporter.
 */
public class TestMeterRegistryProducer {

    @Produces
    @Singleton
    SimpleMeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}
