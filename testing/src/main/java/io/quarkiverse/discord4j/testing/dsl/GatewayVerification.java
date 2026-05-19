package io.quarkiverse.discord4j.testing.dsl;

import discord4j.core.GatewayDiscordClient;

/**
 * Callback to run assertions against the mock {@code GatewayDiscordClient} after an event was handled.
 *
 * @param <T> the type of checked exception the verification may throw
 */
@FunctionalInterface
public interface GatewayVerification<T extends Throwable> {

    /**
     * Verifies interactions or state on the given mock gateway.
     *
     * @param gateway the mock gateway to verify
     * @throws T if the verification fails
     */
    void verify(GatewayDiscordClient gateway) throws T;
}
