package io.quarkiverse.discord4j.testing.dsl;

/**
 * Validations available after an event has been published and handled.
 */
public interface ValidatableEventHandling {

    /**
     * Asserts that no error was dropped by the Reactor pipeline while handling the published event.
     *
     * @return this, for chaining further validations
     */
    ValidatableEventHandling noErrors();

    /**
     * Runs custom assertions against the mock {@code GatewayDiscordClient} (for example verifying
     * interactions performed by a handler).
     *
     * @param verification the gateway assertions to run
     * @param <T> the type of checked exception the verification may throw
     * @return this, for chaining further validations
     * @throws T if the verification throws
     */
    <T extends Throwable> ValidatableEventHandling gateway(GatewayVerification<T> verification) throws T;
}
