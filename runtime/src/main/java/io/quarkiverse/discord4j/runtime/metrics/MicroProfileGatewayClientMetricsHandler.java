package io.quarkiverse.discord4j.runtime.metrics;

import static io.quarkiverse.discord4j.runtime.metrics.Metrics.*;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.eclipse.microprofile.metrics.*;
import org.reactivestreams.Publisher;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import io.smallrye.metrics.MetricRegistries;
import reactor.core.publisher.Flux;

public class MicroProfileGatewayClientMetricsHandler implements Function<EventDispatcher, Publisher<?>> {
    private static final MetricRegistry METRIC_REGISTRY = MetricRegistries.get(MetricRegistry.Type.VENDOR);

    private void registerGauge(String name, String description) {
        METRIC_REGISTRY.register(Metadata.builder()
                .withName(name)
                .withDescription(description)
                .withType(MetricType.GAUGE)
                .build(), new GaugeImpl());
    }

    private GaugeImpl getGauge(String name) {
        return (GaugeImpl) METRIC_REGISTRY.getMetrics().get(new MetricID(name));
    }

    @Override
    public Publisher<?> apply(EventDispatcher eventDispatcher) {
        registerGauge(GUILDS_METRIC_NAME, GUILDS_METRIC_DESCRIPTION);
        registerGauge(VOICE_CONNECTIONS_METRIC_NAME, VOICE_CONNECTIONS_METRIC_DESCRIPTION);

        Flux<GuildCreateEvent> guildCreates = eventDispatcher.on(GuildCreateEvent.class)
                .doOnNext(event -> getGauge(GUILDS_METRIC_NAME).increment());
        Flux<GuildDeleteEvent> guildDeletes = eventDispatcher.on(GuildDeleteEvent.class)
                .filter(event -> !event.isUnavailable())
                .doOnNext(event -> getGauge(GUILDS_METRIC_NAME).decrement());

        GaugeImpl gauge = getGauge(VOICE_CONNECTIONS_METRIC_NAME);
        Flux<VoiceStateUpdateEvent> voiceStateUpdates = eventDispatcher.on(VoiceStateUpdateEvent.class)
                .filter(event -> {
                    GatewayDiscordClient gatewayClient = event.getClient();
                    return event.getCurrent().getUserId().equals(gatewayClient.getSelfId());
                })
                .doOnNext(event -> {
                    if (event.isJoinEvent()) {
                        gauge.increment();
                    } else if (event.isLeaveEvent()) {
                        gauge.decrement();
                    }
                });

        return Flux.merge(guildCreates, guildDeletes, voiceStateUpdates);
    }

    private static class GaugeImpl implements Gauge<Long> {
        private final AtomicLong value = new AtomicLong();

        public void increment() {
            value.incrementAndGet();
        }

        public void decrement() {
            value.decrementAndGet();
        }

        @Override
        public Long getValue() {
            return value.get();
        }
    }
}
