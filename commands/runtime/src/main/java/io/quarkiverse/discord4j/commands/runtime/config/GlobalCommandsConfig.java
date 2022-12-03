package io.quarkiverse.discord4j.commands.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class GlobalCommandsConfig {
    /**
     * Whether to serialize and register the JSON files found in {@code path} as global commands on startup.
     */
    @ConfigItem
    public boolean overwriteOnStart;

    /**
     * The path to the JSON files of the global commands.
     */
    @ConfigItem(defaultValue = "META-INF/commands")
    public String path;

    /**
     * Whether to delete registered global commands whose JSON representation is not found in {@code path}.
     */
    @ConfigItem
    public boolean deleteMissing;
}
