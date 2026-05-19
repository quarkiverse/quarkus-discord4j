package io.quarkiverse.discord4j.it;

import static io.quarkiverse.discord4j.testing.Discord4jTesting.defaultShardInfo;
import static io.quarkiverse.discord4j.testing.Discord4jTesting.given;
import static io.quarkiverse.discord4j.testing.Discord4jTesting.mockGateway;
import static io.quarkiverse.discord4j.testing.Discord4jTesting.payloadFromClasspath;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.time.Duration;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;
import io.quarkiverse.discord4j.testing.Discord4jTest;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Discord4jTest
public class Discord4jTestingDslTest {

    @Inject
    ObjectMapper mapper;

    @BeforeEach
    void resetCounter() {
        MessageHandler.received.set(0);
    }

    @Test
    void publishedEventReachesHandler() throws Exception {
        given().when()
                .publish(buildMessageCreateEvent())
                .then()
                .noErrors();

        await().atMost(Duration.ofSeconds(5))
                .untilAtomic(MessageHandler.received, greaterThanOrEqualTo(1));
    }

    private MessageCreateEvent buildMessageCreateEvent() throws Exception {
        MessageData data = mapper.readValue(payloadFromClasspath("/message-created.json"), MessageData.class);
        return new MessageCreateEvent(
                mockGateway(),
                defaultShardInfo(),
                new Message(mockGateway(), data),
                null, null);
    }
}
