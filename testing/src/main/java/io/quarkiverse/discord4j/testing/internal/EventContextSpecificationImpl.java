package io.quarkiverse.discord4j.testing.internal;

import io.quarkiverse.discord4j.testing.dsl.EventContextSpecification;
import io.quarkiverse.discord4j.testing.dsl.EventPublisher;
import io.quarkiverse.discord4j.testing.dsl.GatewaySetup;

public final class EventContextSpecificationImpl implements EventContextSpecification {

    private final Discord4jTestingContext context;

    public EventContextSpecificationImpl(Discord4jTestingContext context) {
        this.context = context;
        context.capturedErrors.clear();
    }

    @Override
    public <T extends Throwable> EventContextSpecification gateway(GatewaySetup<T> setup) throws T {
        setup.setup(context.gateway);
        return this;
    }

    @Override
    public EventPublisher when() {
        return new EventPublisherImpl(context);
    }
}
