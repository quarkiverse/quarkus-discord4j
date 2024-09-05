package io.quarkiverse.discord4j.runtime.config;

import java.util.Optional;

import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Status;

public interface PresenceConfig {
    /**
     * The status of the bot.
     */
    Optional<Status> status();

    /**
     * Activity configuration
     */
    Optional<ActivityConfig> activity();

    interface ActivityConfig {
        /**
         * The type of activity.
         */
        Activity.Type type();

        /**
         * The name of the activity.
         */
        String name();

        /**
         * The YouTube or Twitch URL of the stream, if the activity type is {@code streaming}.
         */
        Optional<String> url();
    }
}
