package io.quarkiverse.discord4j.testing.internal;

import java.util.List;
import java.util.stream.Collectors;

import io.quarkiverse.discord4j.testing.dsl.GatewayVerification;
import io.quarkiverse.discord4j.testing.dsl.ValidatableEventHandling;

final class ValidatableEventHandlingImpl implements ValidatableEventHandling {

    private final Discord4jTestingContext context;

    ValidatableEventHandlingImpl(Discord4jTestingContext context) {
        this.context = context;
    }

    @Override
    public ValidatableEventHandling noErrors() {
        List<Throwable> errors = context.capturedErrors;
        if (!errors.isEmpty()) {
            String details = errors.stream()
                    .map(t -> t.getClass().getName() + ": " + t.getMessage())
                    .collect(Collectors.joining(System.lineSeparator()));
            AssertionError error = new AssertionError(
                    "Expected no errors from event handlers but got " + errors.size() + ":" + System.lineSeparator() + details);
            errors.forEach(error::addSuppressed);
            throw error;
        }
        return this;
    }

    @Override
    public <T extends Throwable> ValidatableEventHandling gateway(GatewayVerification<T> verification) throws T {
        verification.verify(context.gateway);
        return this;
    }
}
