package io.quarkiverse.discord4j.runtime.config;

import java.util.Optional;

import discord4j.core.retriever.EntityRetrievalStrategy;
import discord4j.gateway.intent.Intent;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class Discord4jConfig {
    /**
     * The bot token used to authenticate to the Discord API.
     */
    @ConfigItem
    public String token;

    /**
     * Presence configuration
     */
    @ConfigItem
    public PresenceConfig presence;

    /**
     * The Gateway intents that should be enabled. Specific Gateway intents are required to receive certain Gateway
     * events. Non-privileged intents will be enabled by default.
     */
    @ConfigItem
    public Optional<Intent[]> enabledIntents;

    /**
     * The strategy to use for retrieving Discord entities. Default is {@code cache-fallback-rest}.
     */
    @ConfigItem
    public Optional<EntityRetrievalStrategy> entityRetrievalStrategy;

    /**
     * Sharding configuration
     */
    @ConfigItem
    public ShardingConfig sharding;
}
