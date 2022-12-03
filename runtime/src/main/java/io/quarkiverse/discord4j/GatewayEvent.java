package io.quarkiverse.discord4j;

import java.lang.annotation.*;

/**
 * Identifies the event parameter of a Gateway listener method.
 * <p>
 * A Gateway listener method is a non-private, concrete instance method of a bean class returning
 * {@code Mono}, {@code Uni}, {@code Flux}, or {@code Multi}.
 * <p>
 * The annotated type must be a concrete subclass of {@link discord4j.core.event.domain.Event}.
 * <p>
 * The event parameter must be the first and only event parameter in the method's signature. Any additional parameters
 * in Gateway listener methods must be beans and are treated as injection points by the container.
 * <p>
 * Any bean may declare multiple Gateway listener methods.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GatewayEvent {
}
