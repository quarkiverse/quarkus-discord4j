package io.quarkiverse.discord4j.commands.runtime;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.InteractionCreateEvent;
import io.quarkiverse.discord4j.commands.runtime.config.Discord4jCommandsConfig;

@ApplicationScoped
public class Discord4jCommandsFilter {
    private static final Logger LOGGER = Logger.getLogger(Discord4jCommandsFilter.class);

    @Inject
    Discord4jCommandsConfig config;

    public boolean test(InteractionCreateEvent event, String name, Optional<String> guild, Optional<String> subCommandGroup,
            Optional<String> subCommand) {
        if (!guild.flatMap(value -> Optional.ofNullable(config.guildCommands.get(value)))
                .map(i -> Snowflake.of(i.guildId))
                .equals(event.getInteraction().getGuildId())) {
            return false;
        }

        return event.getInteraction().getCommandInteraction()
                .filter(interaction -> interaction.getName().map(s -> s.equals(name)).orElse(false))
                .map(interaction -> subCommandGroup.map(s -> interaction.getOption(s)
                        .flatMap(option -> subCommand.flatMap(option::getOption)).isPresent())
                        .orElseGet(() -> subCommand.map(s -> interaction.getOption(s).isPresent()).orElse(true)))
                .get();
    }
}
