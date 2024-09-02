package io.quarkiverse.discord4j.commands.runtime.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface GuildCommandsConfig {
    /**
     * Whether to serialize and register the JSON files found in {@code path} as commands in this guild on startup.
     */
    @WithDefault("false")
    boolean overwriteOnStart();

    /**
     * The path to the JSON files of the guild commands. By default, the path is
     * {@code <global-commands.path> + / + <config-name>}
     */
    Optional<String> path();

    /**
     * Whether to delete commands registered in this guild whose JSON representation is not found in {@code path}.
     */
    @WithDefault("false")
    boolean deleteMissing();

    /**
     * The ID of the guild.
     */
    @WithDefault("01")
    long guildId();
}
