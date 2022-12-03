package io.quarkiverse.discord4j.deployment.spi;

import java.util.function.Function;

import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.gizmo.MethodCreator;
import io.quarkus.gizmo.ResultHandle;

public final class GatewayEventSubscriberFlatMapOperatorBuildItem extends MultiBuildItem {
    private final String eventClassName;
    private final Function<MethodCreator, ResultHandle> flatMapArgCreator;

    public GatewayEventSubscriberFlatMapOperatorBuildItem(String eventClassName,
            Function<MethodCreator, ResultHandle> flatMapArgCreator) {
        this.eventClassName = eventClassName;
        this.flatMapArgCreator = flatMapArgCreator;
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public Function<MethodCreator, ResultHandle> getFlatMapArgCreator() {
        return flatMapArgCreator;
    }
}
