package io.quarkiverse.discord4j.runtime.converter;

import jakarta.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

import discord4j.core.retriever.EntityRetrievalStrategy;
import io.quarkus.runtime.configuration.ConverterSupport;

/** Converts a string into an {@link EntityRetrievalStrategy} */
@Priority(ConverterSupport.DEFAULT_QUARKUS_CONVERTER_PRIORITY)
public class EntityRetrievalStrategyConverter implements Converter<EntityRetrievalStrategy> {

    @Override
    public EntityRetrievalStrategy convert(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        switch (value) {
            case "store":
                return EntityRetrievalStrategy.STORE;
            case "store-fallback-rest":
                return EntityRetrievalStrategy.STORE_FALLBACK_REST;
            case "rest":
                return EntityRetrievalStrategy.REST;
            default:
                throw new IllegalArgumentException(String.format("%s is not a supported entity retrieval strategy", value));
        }
    }
}
