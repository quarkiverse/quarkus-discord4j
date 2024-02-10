package io.quarkiverse.discord4j.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import jakarta.inject.Inject;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.voice.VoiceConnection;
import io.quarkiverse.discord4j.runtime.metrics.Metrics;
import io.quarkus.test.QuarkusUnitTest;

public class Discord4jMetricsTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("metrics-application.properties");

    @Inject
    GatewayDiscordClient gateway;

    @RegistryType(type = Type.VENDOR)
    MetricRegistry registry;

    @AfterEach
    void cleanup() {
        if (gateway != null) {
            gateway.logout().block();
        }
    }

    @Test
    public void testMetrics() {
        List<Guild> guilds = gateway.getGuilds().collectList().block();
        assertNotNull(guilds);
        assertEquals(guilds.size(), gauge(Metrics.GUILDS_METRIC_NAME).intValue());

        List<VoiceConnection> voiceConnections = gateway.getGuilds().flatMap(Guild::getVoiceConnection).collectList().block();
        assertNotNull(voiceConnections);
        assertEquals(voiceConnections.size(), gauge(Metrics.VOICE_CONNECTIONS_METRIC_NAME).intValue());
    }

    private Long gauge(String name) {
        Gauge<?> gauge = registry.getGauge(new MetricID(name));
        if (gauge == null) {
            throw new NullPointerException();
        }
        return (Long) gauge.getValue();
    }
}
