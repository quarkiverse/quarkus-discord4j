package io.quarkiverse.discord4j.testing.internal;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public final class Discord4jTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> configProperties = new HashMap<>();
        configProperties.put("quarkus.discord4j.mock.enabled", "true");
        Discord4jTestCallback.enable(configProperties);
        return configProperties;
    }

    @Override
    public void stop() {
    }
}
