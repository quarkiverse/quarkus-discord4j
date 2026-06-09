package io.quarkiverse.discord4j.testing.dsl;

import discord4j.core.event.domain.Event;

/**
 * Publishes a Discord4J {@link Event} into the mock gateway's {@code EventDispatcher} so that the
 * application's {@code @GatewayEvent} handlers receive it as if it came from the real gateway.
 * <p>
 * Obtained from {@link EventContextSpecification#when()}. Build the event with the helpers in
 * {@code Discord4jTesting} (for example {@code mockGateway()} and {@code payloadFromClasspath(...)}),
 * then chain into validations:
 *
 * <pre>{@code
 * given().when()
 *         .publish(messageCreateEvent)
 *         .then()
 *         .noErrors();
 * }</pre>
 */
public interface EventPublisher {

    /**
     * Publishes the given event into the mock gateway's dispatcher.
     *
     * @param event the Discord4J event to deliver to the application handlers
     * @return a response to chain into {@link EventHandlingResponse#then()} validations
     */
    EventHandlingResponse publish(Event event);
}
