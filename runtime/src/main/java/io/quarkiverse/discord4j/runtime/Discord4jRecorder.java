package io.quarkiverse.discord4j.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jakarta.enterprise.context.spi.CreationalContext;

import org.reactivestreams.Publisher;

import discord4j.common.ReactorResources;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.dispatch.DispatchContext;
import discord4j.core.event.dispatch.DispatchEventMapper;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.core.shard.DefaultShardingStrategy;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.core.shard.ShardingStrategy;
import discord4j.discordjson.json.gateway.Ready;
import discord4j.discordjson.json.gateway.Resumed;
import discord4j.gateway.GatewayOptions;
import discord4j.gateway.GatewayReactorResources;
import discord4j.gateway.intent.IntentSet;
import discord4j.gateway.retry.GatewayStateChange;
import discord4j.voice.VoiceReactorResources;
import io.netty.channel.EventLoopGroup;
import io.quarkiverse.discord4j.runtime.config.Discord4jConfig;
import io.quarkiverse.discord4j.runtime.config.PresenceConfig;
import io.quarkiverse.discord4j.runtime.metrics.MicroProfileGatewayClientMetricsHandler;
import io.quarkiverse.discord4j.runtime.metrics.MicrometerGatewayClientMetricsHandler;
import io.quarkus.arc.Arc;
import io.quarkus.arc.BeanDestroyer;
import io.quarkus.netty.MainEventLoopGroup;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.runtime.metrics.MetricsFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.udp.UdpClient;

@Recorder
public class Discord4jRecorder {
    private static volatile Mono<Event> lastEvent;
    public static volatile Supplier<CompletableFuture<Boolean>> hotReplacementHandler;

    private static Function<EventDispatcher, Publisher<?>> metricsHandler;
    private static List<Function<ReadyEvent, Publisher<?>>> readyEventFunctions = new ArrayList<>();

    private static Object getBeanInstance(String className) {
        try {
            Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(className);
            return Arc.container().instance(cl).get();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ClientPresence getPresence(PresenceConfig presenceConfig) {
        return ClientPresence.of(presenceConfig.status.orElse(Status.ONLINE), presenceConfig.activity
                .map(activity -> ClientActivity.of(activity.type, activity.name, activity.url.orElse(null)))
                .orElse(null));
    }

    public void setupMetrics(String type) {
        if (type.equals(MetricsFactory.MICROMETER)) {
            metricsHandler = new MicrometerGatewayClientMetricsHandler();
        } else {
            metricsHandler = new MicroProfileGatewayClientMetricsHandler();
        }
    }

    public Supplier<EventLoopGroup> getEventLoopGroupBean() {
        return new Supplier<EventLoopGroup>() {
            @Override
            public EventLoopGroup get() {
                return Arc.container().instance(EventLoopGroup.class, MainEventLoopGroup.Literal.INSTANCE).get();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void setReadyEventFunctions(List<String> classNames) {
        readyEventFunctions = classNames.stream()
                .map(className -> (Function<ReadyEvent, Publisher<?>>) getBeanInstance(className))
                .collect(Collectors.toList());
    }

    // TODO jackson object mapper?
    public Supplier<DiscordClient> createDiscordClient(Discord4jConfig config, boolean ssl, ExecutorService executorService,
            Supplier<EventLoopGroup> eventLoopGroup) {
        return new Supplier<>() {
            @Override
            public DiscordClient get() {
                HttpClient httpClient = HttpClient.create().runOn(eventLoopGroup.get()).compress(true).followRedirect(true);
                return DiscordClient.builder(config.token)
                        .setReactorResources(ReactorResources.builder()
                                .httpClient(ssl ? httpClient.secure() : httpClient)
                                .blockingTaskScheduler(Schedulers.fromExecutorService(executorService))
                                .build())
                        .build();
            }
        };
    }

    public Supplier<GatewayDiscordClient> createGatewayClient(Discord4jConfig config,
            Supplier<DiscordClient> discordClientSupplier) {
        return new Supplier<>() {
            @Override
            public GatewayDiscordClient get() {
                GatewayBootstrap<GatewayOptions> bootstrap = discordClientSupplier.get().gateway();
                bootstrap.setGatewayReactorResources(GatewayReactorResources::new);
                bootstrap.setVoiceReactorResources(reactorResources -> VoiceReactorResources.builder(reactorResources)
                        .udpClient(UdpClient.create().runOn(reactorResources.getHttpClient().configuration().loopResources()))
                        .build());

                bootstrap.setInitialPresence(shard -> getPresence(config.presence));
                config.enabledIntents.ifPresent(intents -> bootstrap.setEnabledIntents(IntentSet.of(intents)));
                config.entityRetrievalStrategy.ifPresent(bootstrap::setEntityRetrievalStrategy);

                DefaultShardingStrategy.Builder shardBuilder = ShardingStrategy.builder();
                config.sharding.count.ifPresent(shardBuilder::count);
                config.sharding.indices.ifPresent(shardBuilder::indices);
                config.sharding.maxConcurrency.ifPresent(shardBuilder::maxConcurrency);
                bootstrap.setSharding(shardBuilder.build());

                if (hotReplacementHandler != null) {
                    DispatchEventMapper emitter = DispatchEventMapper.emitEvents();
                    bootstrap.setDispatchEventMapper(new DispatchEventMapper() {
                        @Override
                        @SuppressWarnings("unchecked")
                        public <D, S, E extends Event> Mono<E> handle(DispatchContext<D, S> context) {
                            Class<?> dispatch = context.getDispatch().getClass();
                            if (Ready.class.isAssignableFrom(dispatch) || Resumed.class.isAssignableFrom(dispatch) ||
                                    GatewayStateChange.class.isAssignableFrom(dispatch)) {
                                return emitter.handle(context);
                            }

                            Mono<E> event = emitter.handle(context);
                            lastEvent = (Mono<Event>) event;

                            return Mono.fromFuture(hotReplacementHandler.get())
                                    .flatMap(restarted -> restarted ? Mono.empty() : event);
                        }
                    });
                }

                bootstrap.withEventDispatcher(dispatcher -> {
                    List<Publisher<?>> sources = new ArrayList<>();
                    if (lastEvent != null) {
                        sources.add(
                                dispatcher.on(ReadyEvent.class).flatMap(ignored -> lastEvent).doOnNext(dispatcher::publish));
                    }

                    if (metricsHandler != null) {
                        sources.add(metricsHandler.apply(dispatcher));
                    }

                    for (Function<ReadyEvent, Publisher<?>> readyEventFunction : readyEventFunctions) {
                        sources.add(dispatcher.on(ReadyEvent.class).flatMap(readyEventFunction));
                    }

                    return Flux.concat(sources);
                });

                return bootstrap.login().block();
            }
        };
    }

    public static class GatewayDiscordClientDestroyer implements BeanDestroyer<GatewayDiscordClient> {

        @Override
        public void destroy(GatewayDiscordClient instance, CreationalContext<GatewayDiscordClient> context,
                Map<String, Object> params) {
            instance.logout().block();
        }
    }
}
