package io.quarkiverse.discord4j.runtime.config;

import discord4j.core.retriever.EntityRetrievalStrategy;
import discord4j.core.retriever.FallbackEntityRetriever;
import discord4j.core.retriever.RestEntityRetriever;
import discord4j.core.retriever.StoreEntityRetriever;

public enum EntityRetrievalStrategyConfig {
    /**
     * The strategy to use for retrieving Discord entities. Default is {@code cache-fallback-rest}.
     */
    STORE(StoreEntityRetriever::new),

    /**
     * Strategy that consists of retrieving entities from REST API.
     */
    REST(RestEntityRetriever::new),

    /**
     * Strategy that consists of retrieving entities from stores first, then hit the REST API if not found.
     */
    STORE_FALLBACK_REST(gateway -> new FallbackEntityRetriever(new StoreEntityRetriever(gateway),
            new RestEntityRetriever(gateway)));

    private final EntityRetrievalStrategy strategy;

    EntityRetrievalStrategyConfig(final EntityRetrievalStrategy strategy) {
        this.strategy = strategy;
    }

    public EntityRetrievalStrategy strategy() {
        return strategy;
    }
}
