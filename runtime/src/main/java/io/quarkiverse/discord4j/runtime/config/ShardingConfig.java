package io.quarkiverse.discord4j.runtime.config;

import java.util.Optional;
import java.util.OptionalInt;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class ShardingConfig {
    /**
     * The number of shards that this bot should be split into.
     */
    @ConfigItem
    public OptionalInt count;

    /**
     * Which shards from the configured number of shards that this bot will receive events from. The bot will
     * receive events from all specified shards by default.
     */
    @ConfigItem
    public Optional<int[]> indices;

    /**
     * The number of shards that this bot will concurrently identify to the Gateway.
     *
     * [WARNING]
     * ===
     * This property should only ever be configured if the bot is allowed to use
     * https://discord.com/developers/docs/topics/gateway#sharding-for-very-large-bots[very large sharding].
     * Otherwise, the bot will incur a rate limit when identifying to the Gateway.
     * ===
     *
     * @asciidoclet
     */
    @ConfigItem
    public OptionalInt maxConcurrency;
}
