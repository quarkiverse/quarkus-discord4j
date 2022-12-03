package io.quarkiverse.discord4j.deployment;

import org.jboss.jandex.DotName;

import discord4j.core.event.domain.Event;
import io.quarkiverse.discord4j.GatewayEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Discord4jDotNames {
    public static final DotName EVENT = DotName.createSimple(Event.class.getName());
    public static final DotName FLUX = DotName.createSimple(Flux.class.getName());
    public static final DotName GATEWAY_EVENT = DotName.createSimple(GatewayEvent.class.getName());
    public static final DotName MONO = DotName.createSimple(Mono.class.getName());
    public static final DotName MULTI = DotName.createSimple(Multi.class.getName());
    public static final DotName UNI = DotName.createSimple(Uni.class.getName());

    private Discord4jDotNames() {
    }
}
