package io.quarkiverse.discord4j.commands.runtime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.InteractionCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.Interaction;
import io.quarkiverse.discord4j.commands.runtime.config.Discord4jCommandsConfig;
import io.quarkiverse.discord4j.commands.runtime.config.GuildCommandsConfig;

class Discord4jCommandsFilterTest {

    private static final Snowflake GUILD = Snowflake.of(123L);
    private static final Snowflake OTHER_GUILD = Snowflake.of(456L);

    @Test
    void globalCommandMatchesInsideAGuild() {
        Discord4jCommandsFilter filter = filter(Map.of());
        InteractionCreateEvent event = interaction(Optional.of(GUILD), "ping");

        assertTrue(filter.test(event, "ping", Optional.of(""), Optional.empty(), Optional.empty()));
    }

    @Test
    void globalCommandMatchesInDirectMessage() {
        Discord4jCommandsFilter filter = filter(Map.of());
        InteractionCreateEvent event = interaction(Optional.empty(), "ping");

        assertTrue(filter.test(event, "ping", Optional.of(""), Optional.empty(), Optional.empty()));
    }

    @Test
    void guildCommandMatchesItsConfiguredGuild() {
        Discord4jCommandsFilter filter = filter(Map.of("test", guildConfig(GUILD.asLong())));
        InteractionCreateEvent event = interaction(Optional.of(GUILD), "ping");

        assertTrue(filter.test(event, "ping", Optional.of("test"), Optional.empty(), Optional.empty()));
    }

    @Test
    void guildCommandRejectedInAnotherGuild() {
        Discord4jCommandsFilter filter = filter(Map.of("test", guildConfig(GUILD.asLong())));
        InteractionCreateEvent event = interaction(Optional.of(OTHER_GUILD), "ping");

        assertFalse(filter.test(event, "ping", Optional.of("test"), Optional.empty(), Optional.empty()));
    }

    private Discord4jCommandsFilter filter(Map<String, GuildCommandsConfig> guildCommands) {
        Discord4jCommandsConfig config = mock(Discord4jCommandsConfig.class);
        when(config.guildCommands()).thenReturn(guildCommands);
        Discord4jCommandsFilter filter = new Discord4jCommandsFilter();
        filter.config = config;
        return filter;
    }

    private GuildCommandsConfig guildConfig(long guildId) {
        GuildCommandsConfig config = mock(GuildCommandsConfig.class);
        when(config.guildId()).thenReturn(java.util.OptionalLong.of(guildId));
        return config;
    }

    private InteractionCreateEvent interaction(Optional<Snowflake> guildId, String commandName) {
        InteractionCreateEvent event = mock(InteractionCreateEvent.class);
        Interaction interaction = mock(Interaction.class);
        ApplicationCommandInteraction commandInteraction = mock(ApplicationCommandInteraction.class);
        when(event.getInteraction()).thenReturn(interaction);
        when(interaction.getGuildId()).thenReturn(guildId);
        when(interaction.getCommandInteraction()).thenReturn(Optional.of(commandInteraction));
        when(commandInteraction.getName()).thenReturn(Optional.of(commandName));
        return event;
    }
}
