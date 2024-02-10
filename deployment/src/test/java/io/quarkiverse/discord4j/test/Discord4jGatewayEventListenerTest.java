package io.quarkiverse.discord4j.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import jakarta.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;
import discord4j.gateway.ShardInfo;
import io.quarkiverse.discord4j.GatewayEvent;
import io.quarkus.test.QuarkusUnitTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Discord4jGatewayEventListenerTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar.addClass(MyBean.class).addAsResource("message.json"))
            .setLogRecordPredicate(lr -> lr.getLoggerName().equals(MyBean.class.getName()))
            .withConfigurationResource("application.properties");

    @Inject
    GatewayDiscordClient gateway;

    @Inject
    ObjectMapper objectMapper;

    @AfterEach
    void cleanup() {
        if (gateway != null) {
            gateway.logout().block();
        }
    }

    @Test
    public void testListener() throws IOException {
        MessageData data = objectMapper.readValue(
                Thread.currentThread().getContextClassLoader().getResource("message.json"),
                MessageData.class);
        gateway.getEventDispatcher()
                .publish(new MessageCreateEvent(gateway, ShardInfo.create(0, 1), new Message(gateway, data), null, null));

        config.assertLogRecords(lr -> assertEquals(4, lr.size()));
    }

    static class MyBean {
        private static final Logger LOGGER = Logger.getLogger(MyBean.class);

        Mono<Void> monoMessage(@GatewayEvent MessageCreateEvent messageCreate) {
            return Mono.fromRunnable(() -> LOGGER.info("Received MessageCreateEvent"));
        }

        Uni<Void> uniMessage(@GatewayEvent MessageCreateEvent messageCreate) {
            return Uni.createFrom().voidItem().invoke(() -> LOGGER.info("Received MessageCreateEvent"));
        }

        Flux<Void> fluxMessage(@GatewayEvent MessageCreateEvent messageCreate) {
            return Mono.fromRunnable(() -> LOGGER.info("Received MessageCreateEvent")).thenMany(Flux.empty());
        }

        Multi<Void> multiMessage(@GatewayEvent MessageCreateEvent messageCreate) {
            return Uni.createFrom().voidItem().invoke(() -> LOGGER.info("Received MessageCreateEvent")).toMulti();
        }
    }
}
