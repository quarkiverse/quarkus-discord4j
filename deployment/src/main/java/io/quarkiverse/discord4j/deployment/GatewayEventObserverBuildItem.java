package io.quarkiverse.discord4j.deployment;

import java.util.List;

import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import io.quarkus.builder.item.MultiBuildItem;

public final class GatewayEventObserverBuildItem extends MultiBuildItem {
    private final DotName className;
    private final String returnType;
    private final String method;
    private final String eventClassName;
    private final List<Type> injectionPoints;

    public GatewayEventObserverBuildItem(DotName className, String returnType, String method,
            String eventClassName, List<Type> injectionPoints) {
        this.className = className;
        this.returnType = returnType;
        this.method = method;
        this.eventClassName = eventClassName;
        this.injectionPoints = injectionPoints;
    }

    public DotName getClassName() {
        return className;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getMethod() {
        return method;
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public List<Type> getInjectionPoints() {
        return injectionPoints;
    }

    public boolean observesReadyEvent() {
        return eventClassName.equals(ReadyEvent.class.getName());
    }
}
