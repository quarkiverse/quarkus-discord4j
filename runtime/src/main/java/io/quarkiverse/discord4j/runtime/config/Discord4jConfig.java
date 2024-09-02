package io.quarkiverse.discord4j.runtime.config;

import java.util.Optional;

import discord4j.core.retriever.EntityRetrievalStrategy;
import discord4j.core.retriever.FallbackEntityRetriever;
import discord4j.core.retriever.RestEntityRetriever;
import discord4j.core.retriever.StoreEntityRetriever;
import discord4j.gateway.intent.Intent;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.discord4j")
public interface Discord4jConfig {
    /**
     * The bot token used to authenticate to the Discord API.
     */
    String token();

    /**
     * Presence configuration
     */
    PresenceConfig presence();

    /**
     * The Gateway intents that should be enabled. Specific Gateway intents are required to receive certain Gateway
     * events. Non-privileged intents will be enabled by default.
     */
    Optional<Intent[]> enabledIntents();

    /**
     * The strategy to use for retrieving Discord entities. Default is {@code cache-fallback-rest}.
     */
    @WithDefault("STORE_FALLBACK_REST")
    EntityRetrievalStrategyType entityRetrievalStrategy();

    enum EntityRetrievalStrategyType {
        STORE(StoreEntityRetriever::new),
        REST(RestEntityRetriever::new),
        STORE_FALLBACK_REST(
                gateway -> new FallbackEntityRetriever(new StoreEntityRetriever(gateway), new RestEntityRetriever(gateway)));

        private final EntityRetrievalStrategy strategy;

        EntityRetrievalStrategyType(EntityRetrievalStrategy strategy) {
            this.strategy = strategy;
        }

        public EntityRetrievalStrategy strategy() {
            return strategy;
        }
    }

    /**
     * Sharding configuration
     */
    ShardingConfig sharding();
}
