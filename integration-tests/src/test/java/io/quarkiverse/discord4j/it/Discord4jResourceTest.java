package io.quarkiverse.discord4j.it;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Discord4jResourceTest {

    @Test
    public void testDiscordBot() {
        when().get("/discord-bot").then().body(is("true"));
    }
}
