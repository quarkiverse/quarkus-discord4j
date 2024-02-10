package io.quarkiverse.discord4j.test;

import static io.restassured.RestAssured.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import discord4j.core.event.domain.message.MessageCreateEvent;
import io.quarkiverse.discord4j.GatewayEvent;
import io.quarkus.test.QuarkusDevModeTest;
import reactor.core.publisher.Mono;

public class Discord4jDevModeTest {

    @RegisterExtension
    static final QuarkusDevModeTest config = new QuarkusDevModeTest()
            .withApplicationRoot(jar -> jar.addClass(Discord4jDevModeTestEndpoint.class)
                    .addAsResource("message.json")
                    .addAsResource("application.properties"))
            .setLogRecordPredicate(lr -> lr.getLoggerName().equals(MyBean.class.getName()));

    @Test
    public void testLiveReload() {
        get("/discord");
        assertEquals(0, config.getLogRecords().size());

        config.addSourceFile(MyBean.class);

        get("/discord");
        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> config.getLogRecords().size() == 1);
    }

    static class MyBean {
        private static final Logger LOGGER = Logger.getLogger(MyBean.class);

        Mono<Void> onMessageCreate(@GatewayEvent MessageCreateEvent event) {
            return Mono.fromRunnable(() -> LOGGER.info("Received MessageCreate"));
        }
    }
}
