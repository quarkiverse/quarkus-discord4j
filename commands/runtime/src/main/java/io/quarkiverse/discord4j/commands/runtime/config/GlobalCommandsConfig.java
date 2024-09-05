package io.quarkiverse.discord4j.commands.runtime.config;

import io.smallrye.config.WithDefault;

public interface GlobalCommandsConfig {
    /**
     * Whether to serialize and register the JSON files found in {@code path} as global commands on startup.
     */
    @WithDefault("false")
    boolean overwriteOnStart();

    /**
     * The path to the JSON files of the global commands.
     */
    @WithDefault("META-INF/commands")
    String path();

    /**
     * Whether to delete registered global commands whose JSON representation is not found in {@code path}.
     */
    @WithDefault("false")
    boolean deleteMissing();
}
