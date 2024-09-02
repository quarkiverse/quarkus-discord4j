package io.quarkiverse.discord4j.runtime.converter;

import jakarta.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

import discord4j.core.object.presence.Status;
import io.quarkus.runtime.configuration.ConverterSupport;

/**
 * Converts a string into a
 * <a href="https://discord.com/developers/docs/topics/gateway#update-presence-status-types">Gateway status</a>
 */
@Priority(ConverterSupport.DEFAULT_QUARKUS_CONVERTER_PRIORITY)
public class StatusConverter implements Converter<Status> {

    @Override
    public Status convert(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return switch (value.toLowerCase()) {
            case "online" -> Status.ONLINE;
            case "idle" -> Status.IDLE;
            case "dnd" -> Status.DO_NOT_DISTURB;
            case "invisible" -> Status.INVISIBLE;
            default -> throw new IllegalArgumentException(String.format("%s is not a supported status", value));
        };
    }
}
