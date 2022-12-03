package io.quarkiverse.discord4j.runtime.config;

import java.util.Optional;

import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Status;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class PresenceConfig {
    /**
     * The status of the bot.
     */
    @ConfigItem
    public Optional<Status> status;

    /**
     * Activity configuration
     */
    @ConfigItem
    public Optional<ActivityConfig> activity;

    @ConfigGroup
    public static class ActivityConfig {
        /**
         * The type of activity.
         */
        @ConfigItem
        public Activity.Type type;

        /**
         * The name of the activity.
         */
        @ConfigItem
        public String name;

        /**
         * The YouTube or Twitch URL of the stream, if the activity type is {@code streaming}.
         */
        @ConfigItem
        public Optional<String> url;
    }
}
