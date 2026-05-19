package io.quarkiverse.discord4j.testing.dsl;

import discord4j.core.GatewayDiscordClient;

/**
 * Callback to stub the mock {@code GatewayDiscordClient} before events are published.
 *
 * @param <T> the type of checked exception the setup may throw
 */
@FunctionalInterface
public interface GatewaySetup<T extends Throwable> {

    /**
     * Stubs the given mock gateway.
     *
     * @param gateway the mock gateway to configure
     * @throws T if the setup fails
     */
    void setup(GatewayDiscordClient gateway) throws T;
}
