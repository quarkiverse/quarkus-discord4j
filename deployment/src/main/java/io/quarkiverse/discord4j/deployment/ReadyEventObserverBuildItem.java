package io.quarkiverse.discord4j.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class ReadyEventObserverBuildItem extends MultiBuildItem {
    private final String className;

    public ReadyEventObserverBuildItem(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
