package io.quarkiverse.discord4j.commands.runtime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.possible.Possible;

/**
 * Verifies the {@code delete-missing} selection logic that regressed in issue #49: registered commands matching a local
 * JSON definition must be kept, and only commands absent from the local definitions must be selected for deletion.
 */
class Discord4jCommandsFilterMissingTest {

    private static final int CHAT_INPUT = 1;
    private static final int USER = 2;

    @Test
    void keepsCommandWithMatchingLocalDefinition() {
        List<ApplicationCommandRequest> local = List.of(localCommand("ping", CHAT_INPUT));

        assertFalse(Discord4jCommandsRegistrar.filterMissing(local).test(discordCommand("ping", CHAT_INPUT)));
    }

    @Test
    void deletesCommandAbsentFromLocalDefinitions() {
        List<ApplicationCommandRequest> local = List.of(localCommand("ping", CHAT_INPUT));

        assertTrue(Discord4jCommandsRegistrar.filterMissing(local).test(discordCommand("pong", CHAT_INPUT)));
    }

    @Test
    void deletesCommandWhenOnlyTypeDiffers() {
        List<ApplicationCommandRequest> local = List.of(localCommand("ping", CHAT_INPUT));

        assertTrue(Discord4jCommandsRegistrar.filterMissing(local).test(discordCommand("ping", USER)));
    }

    @Test
    void deletesEveryCommandWhenNoLocalDefinitions() {
        assertTrue(Discord4jCommandsRegistrar.filterMissing(List.of()).test(discordCommand("ping", CHAT_INPUT)));
    }

    private ApplicationCommandRequest localCommand(String name, int type) {
        return ApplicationCommandRequest.builder().name(name).type(type).build();
    }

    private ApplicationCommandData discordCommand(String name, int type) {
        ApplicationCommandData command = mock(ApplicationCommandData.class);
        when(command.name()).thenReturn(name);
        when(command.type()).thenReturn(Possible.of(type));
        return command;
    }
}
