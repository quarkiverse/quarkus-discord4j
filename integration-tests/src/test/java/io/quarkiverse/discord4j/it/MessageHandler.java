package io.quarkiverse.discord4j.it;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;

import discord4j.core.event.domain.message.MessageCreateEvent;
import io.quarkiverse.discord4j.GatewayEvent;
import reactor.core.publisher.Mono;

@ApplicationScoped
public class MessageHandler {

    public static final AtomicInteger received = new AtomicInteger();

    Mono<Void> onMessageCreate(@GatewayEvent MessageCreateEvent event) {
        return Mono.fromRunnable(received::incrementAndGet);
    }
}
