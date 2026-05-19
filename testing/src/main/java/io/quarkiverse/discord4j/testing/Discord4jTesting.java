package io.quarkiverse.discord4j.testing;

import java.io.IOException;

import discord4j.core.GatewayDiscordClient;
import discord4j.gateway.ShardInfo;
import io.quarkiverse.discord4j.testing.dsl.EventContextSpecification;
import io.quarkiverse.discord4j.testing.dsl.EventPublisher;
import io.quarkiverse.discord4j.testing.internal.Discord4jTestingContext;
import io.quarkiverse.discord4j.testing.internal.EventContextSpecificationImpl;

public final class Discord4jTesting {

    private Discord4jTesting() {
    }

    public static EventContextSpecification given() {
        return new EventContextSpecificationImpl(Discord4jTestingContext.get());
    }

    public static EventPublisher when() {
        return given().when();
    }

    public static String payloadFromClasspath(String path) throws IOException {
        return Discord4jTestingContext.get().getFromClasspath(path);
    }

    public static ShardInfo defaultShardInfo() {
        return ShardInfo.create(0, 1);
    }

    public static GatewayDiscordClient mockGateway() {
        return Discord4jTestingContext.get().gateway;
    }
}
