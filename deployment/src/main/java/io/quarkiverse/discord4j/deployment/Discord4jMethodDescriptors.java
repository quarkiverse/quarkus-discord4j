package io.quarkiverse.discord4j.deployment;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import discord4j.core.GatewayDiscordClient;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.gizmo.MethodDescriptor;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.uni.ToMono;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Discord4jMethodDescriptors {
    public static final MethodDescriptor ARC_CONTAINER_INSTANCE = MethodDescriptor.ofMethod(ArcContainer.class, "instance",
            InstanceHandle.class, Class.class, Annotation[].class);
    public static final MethodDescriptor FLUX_FLAT_MAP = MethodDescriptor.ofMethod(Flux.class, "flatMap", Flux.class,
            Function.class);
    public static final MethodDescriptor FLUX_THEN = MethodDescriptor.ofMethod(Flux.class, "then", Mono.class);
    public static final MethodDescriptor FUNCTION_APPLY = MethodDescriptor.ofMethod(Function.class, "apply", Object.class,
            Object.class);
    public static final MethodDescriptor GATEWAY_DISCORD_CLIENT_ON = MethodDescriptor.ofMethod(GatewayDiscordClient.class, "on",
            Flux.class, Class.class);
    public static final MethodDescriptor INSTANCE_HANDLE_GET = MethodDescriptor.ofMethod(InstanceHandle.class, "get",
            Object.class);
    public static final MethodDescriptor MONO_SUBSCRIBE = MethodDescriptor.ofMethod(Mono.class, "subscribe", Disposable.class);
    public static final MethodDescriptor MONO_WHEN = MethodDescriptor.ofMethod(Mono.class, "when", Mono.class,
            Publisher[].class);
    public static final MethodDescriptor TO_MONO_APPLY = MethodDescriptor.ofMethod(ToMono.class, "apply", Mono.class,
            Uni.class);
    public static final MethodDescriptor UNI_REACTOR_CONVERTERS_TO_MONO = MethodDescriptor.ofMethod(UniReactorConverters.class,
            "toMono", ToMono.class);

    private Discord4jMethodDescriptors() {
    }
}
