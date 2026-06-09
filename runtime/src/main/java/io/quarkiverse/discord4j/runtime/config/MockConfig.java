package io.quarkiverse.discord4j.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface MockConfig {
    /**
     * Whether mock mode is enabled. When enabled, a standalone {@code EventDispatcher} is created
     * instead of connecting to the Discord Gateway. Useful for testing event handlers without a real bot token.
     */
    @WithDefault("false")
    boolean enabled();
}
