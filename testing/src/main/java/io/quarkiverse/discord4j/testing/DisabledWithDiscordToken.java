package io.quarkiverse.discord4j.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

/**
 * Disables a test (or test class) when a real Discord bot token is available through the
 * {@code QUARKUS_DISCORD4J_TOKEN} environment variable.
 * <p>
 * Use this on tests that assert the no-token behavior (for example, that the gateway is not
 * initialized), so they are skipped when a token is configured and the gateway would start.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@DisabledIfEnvironmentVariable(named = "QUARKUS_DISCORD4J_TOKEN", matches = ".+", disabledReason = "A Discord bot token is configured - the gateway will be initialized")
public @interface DisabledWithDiscordToken {
}
