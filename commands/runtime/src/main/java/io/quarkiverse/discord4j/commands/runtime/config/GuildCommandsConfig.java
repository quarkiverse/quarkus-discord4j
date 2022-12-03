package io.quarkiverse.discord4j.commands.runtime.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class GuildCommandsConfig {
    /**
     * Whether to serialize and register the JSON files found in {@code path} as commands in this guild on startup.
     */
    @ConfigItem
    public boolean overwriteOnStart;

    /**
     * The path to the JSON files of the guild commands. By default, the path is
     * {@code <global-commands.path> + / + <config-name>}
     */
    @ConfigItem
    public Optional<String> path;

    /**
     * Whether to delete commands registered in this guild whose JSON representation is not found in {@code path}.
     */
    @ConfigItem
    public boolean deleteMissing;

    /**
     * The ID of the guild.
     */
    @ConfigItem
    public long guildId;
}
