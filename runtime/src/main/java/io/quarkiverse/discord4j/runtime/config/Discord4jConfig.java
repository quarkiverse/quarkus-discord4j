package io.quarkiverse.discord4j.runtime.config;

import java.util.List;
import java.util.Optional;

import discord4j.gateway.intent.Intent;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.discord4j")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface Discord4jConfig {
    /**
     * The bot token used to authenticate to the Discord API.
     */
    Optional<String> token();

    /**
     * Presence configuration.
     */
    PresenceConfig presence();

    /**
     * The Gateway intents that should be enabled. Specific Gateway intents are required to receive certain Gateway
     * events. Non-privileged intents will be enabled by default.
     */
    Optional<List<Intent>> enabledIntents();

    /**
     * The strategy to use for retrieving Discord entities. Default is {@code cache-fallback-rest}.
     */
    Optional<String> entityRetrievalStrategy();

    /**
     * Mock mode configuration.
     */
    MockConfig mock();

    /**
     * Sharding configuration.
     */
    ShardingConfig sharding();
}
