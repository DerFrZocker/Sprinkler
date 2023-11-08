package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.event.handler.PullRequestEventHandler;

import java.util.HashMap;
import java.util.Map;

public class PullRequestEventManager {

    private final Map<Class<?>, PullRequestEventHandler<?>> eventHandlers = new HashMap<>();

    public <T extends PullRequestEvent> void registerEventHandler(PullRequestEventHandler<T> eventHandler) {
        PullRequestEventHandler<?> other = eventHandlers.get(eventHandler.getEventType());

        if (other != null) {
            throw new IllegalArgumentException(
                    String.format("Event handler for %s is already registered.", eventHandler.getEventType()));
        }

        eventHandlers.put(eventHandler.getEventType(), eventHandler);
    }

    public <T extends PullRequestEvent> void callEvent(T event) {
        PullRequestEventHandler<?> eventHandler = eventHandlers.get(event.getClass());

        if (eventHandler == null) {
            throw new IllegalArgumentException(
                    String.format("No event handler registered for even %s.", event.getClass()));
        }

        if (eventHandler.getEventType() == event.getClass()) {
            ((PullRequestEventHandler<T>) eventHandler).handle(event);
        }
    }
}
