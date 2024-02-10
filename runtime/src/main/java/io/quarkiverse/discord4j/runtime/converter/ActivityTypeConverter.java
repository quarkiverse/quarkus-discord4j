package io.quarkiverse.discord4j.runtime.converter;

import jakarta.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

import discord4j.core.object.presence.Activity;
import io.quarkus.runtime.configuration.ConverterSupport;

/**
 * Converts a string into a
 * <a href="https://discord.com/developers/docs/topics/gateway#activity-object-activity-types">Gateway activity type</a>
 */
@Priority(ConverterSupport.DEFAULT_QUARKUS_CONVERTER_PRIORITY)
public class ActivityTypeConverter implements Converter<Activity.Type> {

    @Override
    public Activity.Type convert(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        switch (value.toLowerCase()) {
            case "playing":
                return Activity.Type.PLAYING;
            case "streaming":
                return Activity.Type.STREAMING;
            case "listening to":
                return Activity.Type.LISTENING;
            case "watching":
                return Activity.Type.WATCHING;
            case "competing in":
                return Activity.Type.COMPETING;
        }
        throw new IllegalStateException(String.format("%s is not a supported activity type", value));
    }
}
