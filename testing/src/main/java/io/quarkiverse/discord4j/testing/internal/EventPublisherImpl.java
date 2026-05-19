package io.quarkiverse.discord4j.testing.internal;

import discord4j.core.event.domain.Event;
import io.quarkiverse.discord4j.testing.dsl.EventHandlingResponse;
import io.quarkiverse.discord4j.testing.dsl.EventPublisher;

final class EventPublisherImpl implements EventPublisher {

    private final Discord4jTestingContext context;

    EventPublisherImpl(Discord4jTestingContext context) {
        this.context = context;
    }

    @Override
    public EventHandlingResponse publish(Event event) {
        context.dispatcher.publish(event);
        return new EventHandlingResponseImpl(context);
    }
}
