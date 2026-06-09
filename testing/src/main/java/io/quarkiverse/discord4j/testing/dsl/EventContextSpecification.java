package io.quarkiverse.discord4j.testing.dsl;

/**
 * Entry point of the testing DSL, obtained from {@code Discord4jTesting.given()}. Optionally stubs the
 * mock gateway before transitioning to the event-publishing phase via {@link #when()}.
 */
public interface EventContextSpecification {

    /**
     * Applies additional stubbing to the mock {@code GatewayDiscordClient} before publishing events.
     *
     * @param setup the gateway stubbing to apply
     * @param <T> the type of checked exception the setup may throw
     * @return this specification, for chaining
     * @throws T if the setup throws
     */
    <T extends Throwable> EventContextSpecification gateway(GatewaySetup<T> setup) throws T;

    /**
     * Transitions to the event-publishing phase.
     *
     * @return the publisher used to dispatch events to the application handlers
     */
    EventPublisher when();
}
