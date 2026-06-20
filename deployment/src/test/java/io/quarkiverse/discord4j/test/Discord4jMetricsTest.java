package io.quarkiverse.discord4j.test;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.voice.VoiceConnection;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkiverse.discord4j.runtime.metrics.Metrics;
import io.quarkiverse.discord4j.testing.EnabledWithDiscordToken;
import io.quarkus.test.QuarkusUnitTest;

@EnabledWithDiscordToken
public class Discord4jMetricsTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar.addClass(TestMeterRegistryProducer.class))
            .withConfigurationResource("metrics-application.properties");

    @Inject
    GatewayDiscordClient gateway;

    @Inject
    MeterRegistry registry;

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
        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertEquals(guilds.size(), gauge(Metrics.GUILDS_METRIC_NAME).intValue()));

        List<VoiceConnection> voiceConnections = gateway.getGuilds().flatMap(Guild::getVoiceConnection).collectList()
                .block();
        assertNotNull(voiceConnections);
        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertEquals(voiceConnections.size(),
                        gauge(Metrics.VOICE_CONNECTIONS_METRIC_NAME).intValue()));
    }

    private Double gauge(String name) {
        return registry.find(name).gauge().value();
    }
}
