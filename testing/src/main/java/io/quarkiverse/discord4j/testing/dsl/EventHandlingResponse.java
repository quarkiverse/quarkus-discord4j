package io.quarkiverse.discord4j.testing.dsl;

/**
 * Returned after publishing an event. Bridges into the validation phase of the DSL via {@link #then()}.
 */
public interface EventHandlingResponse {

    /**
     * Transitions to the validation phase.
     *
     * @return the validations available on the handled event
     */
    ValidatableEventHandling then();
}
