package io.quarkiverse.discord4j.runtime;

import java.util.Optional;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.jboss.logging.Logger;

import discord4j.core.GatewayDiscordClient;
import io.quarkiverse.discord4j.runtime.config.Discord4jConfig;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.Startup;

@Singleton
@Startup
public class Discord4jStarter {

    private static final Logger LOGGER = Logger.getLogger(Discord4jStarter.class);

    @Inject
    Discord4jConfig config;

    @Inject
    Instance<GatewayDiscordClient> gatewayInstance;

    private volatile boolean gatewayInitialized;

    private volatile boolean mockMode;

    @PostConstruct
    void init() {
        if (config.mockEnabled()) {
            LOGGER.info("Discord4J mock mode enabled - gateway will not be initialized");
            mockMode = true;
            gatewayInitialized = true;
            return;
        }

        Optional<String> token = config.token();
        if (token.isPresent()) {
            LOGGER.info("Discord bot token found, initializing gateway connection");
            gatewayInstance.get();
            gatewayInitialized = true;
        } else if (LaunchMode.current() == LaunchMode.TEST) {
            LOGGER.warn("No Discord bot token configured - gateway will not be initialized (test mode)");
        } else {
            throw new IllegalStateException(
                    "quarkus.discord4j.token is required to start the Discord bot in production mode");
        }
    }

    public boolean isGatewayInitialized() {
        return gatewayInitialized;
    }

    public boolean isMockMode() {
        return mockMode;
    }
}
