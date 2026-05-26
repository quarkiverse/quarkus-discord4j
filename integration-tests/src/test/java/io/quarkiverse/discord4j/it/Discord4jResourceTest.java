package io.quarkiverse.discord4j.it;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkiverse.discord4j.testing.DisabledWithDiscordToken;
import io.quarkiverse.discord4j.testing.EnabledWithDiscordToken;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class Discord4jResourceTest {

    @Test
    @DisabledWithDiscordToken
    public void testGatewayNotInitializedWithoutToken() {
        when().get("/discord-bot").then().body(is("false"));
    }

    @Test
    @EnabledWithDiscordToken
    public void testGatewayInitializedWithToken() {
        when().get("/discord-bot").then().body(is("true"));
    }

    @Test
    public void testMockModeDisabledByDefault() {
        when().get("/discord-bot/mock").then().body(is("false"));
    }
}
