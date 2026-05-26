package io.quarkiverse.discord4j.test;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.possible.PossibleModule;
import discord4j.gateway.ShardInfo;
import io.quarkiverse.discord4j.GatewayEvent;
import io.quarkiverse.discord4j.runtime.Discord4jStarter;
import io.quarkus.test.QuarkusUnitTest;
import reactor.core.publisher.Mono;

public class Discord4jMockModeTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar.addClass(MyMockBean.class).addAsResource("message.json"))
            .setLogRecordPredicate(lr -> lr.getLoggerName().equals(MyMockBean.class.getName()))
            .withConfigurationResource("application.properties")
            .overrideConfigKey("quarkus.discord4j.mock-enabled", "true");

    @Inject
    Discord4jStarter starter;

    @Inject
    EventDispatcher eventDispatcher;

    static AtomicInteger counter = new AtomicInteger();

    @Test
    public void testMockModeEnabled() {
        assertTrue(starter.isMockMode());
        assertTrue(starter.isGatewayInitialized());
    }

    @Test
    public void testEventDispatcherAvailable() {
        assertNotNull(eventDispatcher);
    }

    @Test
    public void testEventDispatcherDeliversEvents() throws Exception {
        counter.set(0);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new PossibleModule());
        MessageData data = mapper.readValue(
                Thread.currentThread().getContextClassLoader().getResource("message.json"),
                MessageData.class);
        GatewayDiscordClient gateway = org.mockito.Mockito.mock(GatewayDiscordClient.class);
        MessageCreateEvent event = new MessageCreateEvent(
                gateway, ShardInfo.create(0, 1), new Message(gateway, data), null, null);

        eventDispatcher.publish(event);

        await().atMost(Duration.ofSeconds(5)).untilAtomic(counter, greaterThanOrEqualTo(1));
    }

    static class MyMockBean {
        private static final Logger LOGGER = Logger.getLogger(MyMockBean.class);

        Mono<Void> onMessage(@GatewayEvent MessageCreateEvent messageCreate) {
            return Mono.fromRunnable(() -> {
                LOGGER.info("Received MessageCreateEvent in mock mode");
                counter.incrementAndGet();
            });
        }
    }
}
