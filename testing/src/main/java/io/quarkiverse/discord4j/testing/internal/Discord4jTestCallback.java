package io.quarkiverse.discord4j.testing.internal;

import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import discord4j.core.GatewayDiscordClient;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.callback.QuarkusTestAfterConstructCallback;

public final class Discord4jTestCallback implements QuarkusTestAfterConstructCallback {

    private static final String ENABLED_KEY = "quarkiverse-discord4j-testing.enabled";

    public static void enable(Map<String, String> configProperties) {
        configProperties.put(ENABLED_KEY, "true");
    }

    static boolean isEnabled() {
        return ConfigProviderResolver.instance().getConfig()
                .getOptionalValue(ENABLED_KEY, Boolean.class)
                .orElse(false);
    }

    @Override
    public void afterConstruct(Object testInstance) {
        if (!isEnabled()) {
            Discord4jTestingContext.reset();
            return;
        }
        Discord4jTestingContext.set(testInstance);
        Discord4jTestingContext context = Discord4jTestingContext.get();
        QuarkusMock.installMockForType(context.gateway, GatewayDiscordClient.class);
    }
}
