package io.quarkiverse.discord4j.testing.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mockito.Answers;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import io.quarkiverse.discord4j.testing.Discord4jTest;
import io.quarkus.arc.Arc;
import reactor.core.publisher.Hooks;

public final class Discord4jTestingContext {

    private static Discord4jTestingContext instance;
    private static volatile boolean errorHookInstalled;

    public static void set(Object testInstance) {
        Discord4jTest annotation = testInstance.getClass().getAnnotation(Discord4jTest.class);
        Answers defaultAnswers = annotation != null ? annotation.defaultAnswers() : Answers.RETURNS_DEFAULTS;

        EventDispatcher dispatcher = Arc.container().instance(EventDispatcher.class).get();
        GatewayDiscordClient gateway = mock(GatewayDiscordClient.class, withSettings().defaultAnswer(defaultAnswers));
        when(gateway.getEventDispatcher()).thenReturn(dispatcher);
        when(gateway.on(org.mockito.ArgumentMatchers.<Class<? extends Event>> any()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    Class<? extends Event> eventType = (Class<? extends Event>) invocation.getArgument(0);
                    return dispatcher.on(eventType);
                });

        Discord4jTestingContext ctx = new Discord4jTestingContext(testInstance, dispatcher, gateway);
        Hooks.onErrorDropped(ctx.capturedErrors::add);
        errorHookInstalled = true;
        instance = ctx;
    }

    public static Discord4jTestingContext get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Discord4jTestingContext is not initialized. Ensure the test is annotated with @Discord4jTest.");
        }
        return instance;
    }

    public static void reset() {
        if (errorHookInstalled) {
            Hooks.resetOnErrorDropped();
            errorHookInstalled = false;
        }
        instance = null;
    }

    public final Object testInstance;
    public final EventDispatcher dispatcher;
    public final GatewayDiscordClient gateway;
    public final CopyOnWriteArrayList<Throwable> capturedErrors = new CopyOnWriteArrayList<>();

    private Discord4jTestingContext(Object testInstance, EventDispatcher dispatcher, GatewayDiscordClient gateway) {
        this.testInstance = testInstance;
        this.dispatcher = dispatcher;
        this.gateway = gateway;
    }

    public String getFromClasspath(String path) throws IOException {
        ClassLoader loader = testInstance != null
                ? testInstance.getClass().getClassLoader()
                : Thread.currentThread().getContextClassLoader();
        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        try (InputStream stream = loader.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalArgumentException("No such file in classpath: '" + path + "'");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
