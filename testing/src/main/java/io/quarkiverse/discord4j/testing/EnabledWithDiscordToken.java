package io.quarkiverse.discord4j.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Enables a test (or test class) only when a real Discord bot token is available through the
 * {@code QUARKUS_DISCORD4J_TOKEN} environment variable.
 * <p>
 * Use this on tests that require a live gateway connection so they are skipped (instead of failing)
 * in environments without a token, such as CI without configured secrets.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@EnabledIfEnvironmentVariable(named = "QUARKUS_DISCORD4J_TOKEN", matches = ".+", disabledReason = "Requires a real Discord bot token")
public @interface EnabledWithDiscordToken {
}
