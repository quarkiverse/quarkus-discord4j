package io.quarkiverse.discord4j.commands.runtime.config;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.discord4j")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface Discord4jCommandsConfig {
    /**
     * Global commands configuration
     */
    GlobalCommandsConfig globalCommands();

    /**
     * Guild commands configuration
     */
    Map<String, GuildCommandsConfig> guildCommands();
}
