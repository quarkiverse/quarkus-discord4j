package io.quarkiverse.discord4j.testing.internal;

import io.quarkiverse.discord4j.testing.dsl.EventHandlingResponse;
import io.quarkiverse.discord4j.testing.dsl.ValidatableEventHandling;

final class EventHandlingResponseImpl implements EventHandlingResponse {

    private final Discord4jTestingContext context;

    EventHandlingResponseImpl(Discord4jTestingContext context) {
        this.context = context;
    }

    @Override
    public ValidatableEventHandling then() {
        return new ValidatableEventHandlingImpl(context);
    }
}
