package io.quarkiverse.discord4j.it;

import static io.quarkiverse.discord4j.testing.Discord4jTesting.defaultShardInfo;
import static io.quarkiverse.discord4j.testing.Discord4jTesting.given;
import static io.quarkiverse.discord4j.testing.Discord4jTesting.mockGateway;
import static io.quarkiverse.discord4j.testing.Discord4jTesting.payloadFromClasspath;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.Interaction;
import discord4j.discordjson.json.InteractionData;
import io.quarkiverse.discord4j.commands.Command;
import io.quarkiverse.discord4j.commands.SubCommand;
import io.quarkiverse.discord4j.testing.Discord4jTest;
import io.quarkiverse.discord4j.testing.dsl.ValidatableEventHandling;
import io.quarkus.test.junit.QuarkusTest;
import reactor.core.publisher.Mono;

@QuarkusTest
@Discord4jTest
public class Discord4jMultipleCommandsTest {

    @Inject
    ObjectMapper mapper;

    @BeforeEach
    void resetCounters() {
        StatusCommand.invocations.set(0);
        ActionCommand.invocations.set(0);
    }

    @Test
    void simpleCommandIsRoutedWithoutErrors() throws Exception {
        ValidatableEventHandling response = given().when()
                .publish(interactionEvent("status-interaction.json"))
                .then();

        await().atMost(Duration.ofSeconds(5)).untilAtomic(StatusCommand.invocations, greaterThanOrEqualTo(1));

        response.noErrors();
    }

    @Test
    void subCommandIsRoutedWithoutErrors() throws Exception {
        ValidatableEventHandling response = given().when()
                .publish(interactionEvent("action-do-interaction.json"))
                .then();

        await().atMost(Duration.ofSeconds(5)).untilAtomic(ActionCommand.invocations, greaterThanOrEqualTo(1));

        response.noErrors();
    }

    private ChatInputInteractionEvent interactionEvent(String resource) throws Exception {
        InteractionData data = mapper.readValue(payloadFromClasspath("/" + resource), InteractionData.class);
        return new ChatInputInteractionEvent(mockGateway(), defaultShardInfo(), new Interaction(mockGateway(), data));
    }

    static class StatusCommand {
        static final AtomicInteger invocations = new AtomicInteger();

        @Command("status")
        Mono<Void> onChatInputInteraction(ChatInputInteractionEvent event) {
            return Mono.fromRunnable(invocations::incrementAndGet);
        }
    }

    @Command("action")
    static class ActionCommand {
        static final AtomicInteger invocations = new AtomicInteger();

        @SubCommand("do")
        Mono<Void> onChatInputInteraction(ChatInputInteractionEvent event) {
            return Mono.fromRunnable(invocations::incrementAndGet);
        }
    }
}
