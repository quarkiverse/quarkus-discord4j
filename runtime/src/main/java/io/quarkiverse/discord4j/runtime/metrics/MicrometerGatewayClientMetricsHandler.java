package io.quarkiverse.discord4j.runtime.metrics;

import static io.quarkiverse.discord4j.runtime.metrics.Metrics.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.GuildDeleteEvent;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Flux;

public class MicrometerGatewayClientMetricsHandler implements Function<EventDispatcher, Publisher<?>> {
    private static final MeterRegistry METER_REGISTRY = io.micrometer.core.instrument.Metrics.globalRegistry;
    private final AtomicInteger guilds = new AtomicInteger();
    private final AtomicInteger voiceConnections = new AtomicInteger();

    private void registerGauge(String name, String description, AtomicInteger value) {
        Gauge.builder(name, value, AtomicInteger::doubleValue)
                .description(description)
                .register(METER_REGISTRY);
    }

    @Override
    public Publisher<?> apply(EventDispatcher dispatcher) {
        registerGauge(GUILDS_METRIC_NAME, GUILDS_METRIC_DESCRIPTION, guilds);
        registerGauge(VOICE_CONNECTIONS_METRIC_NAME, VOICE_CONNECTIONS_METRIC_DESCRIPTION, voiceConnections);

        Flux<GuildCreateEvent> guildCreates = dispatcher.on(GuildCreateEvent.class)
                .doOnNext(event -> guilds.incrementAndGet());
        Flux<GuildDeleteEvent> guildDeletes = dispatcher.on(GuildDeleteEvent.class)
                .filter(event -> !event.isUnavailable())
                .doOnNext(event -> guilds.decrementAndGet());

        Flux<VoiceStateUpdateEvent> voiceStateUpdates = dispatcher.on(VoiceStateUpdateEvent.class)
                .filter(event -> {
                    GatewayDiscordClient gatewayClient = event.getClient();
                    return event.getCurrent().getUserId().equals(gatewayClient.getSelfId());
                })
                .doOnNext(event -> {
                    if (event.isJoinEvent()) {
                        voiceConnections.incrementAndGet();
                    } else if (event.isLeaveEvent()) {
                        voiceConnections.decrementAndGet();
                    }
                });

        return Flux.merge(guildCreates, guildDeletes, voiceStateUpdates);
    }
}
