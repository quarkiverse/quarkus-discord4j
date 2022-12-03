package io.quarkiverse.discord4j.runtime.metrics;

public class Metrics {
    public static final String METRICS_PREFIX = "discord4j.";
    public static final String GUILDS_METRIC_NAME = METRICS_PREFIX + "guilds";
    public static final String GUILDS_METRIC_DESCRIPTION = "the amount of guilds this client receives events from";
    public static final String VOICE_CONNECTIONS_METRIC_NAME = METRICS_PREFIX + "voice-connections";
    public static final String VOICE_CONNECTIONS_METRIC_DESCRIPTION = "the amount of voice connections this client is propagating";

    private Metrics() {
    }
}
