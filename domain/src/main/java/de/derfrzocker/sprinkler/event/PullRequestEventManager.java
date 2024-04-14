package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.event.handler.PullRequestEventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PullRequestEventManager {

    private final Map<Class<?>, List<PullRequestEventHandler<?>>> eventHandlers = new HashMap<>();

    public <T extends PullRequestEvent> void registerEventHandler(PullRequestEventHandler<T> eventHandler) {
        eventHandlers.computeIfAbsent(eventHandler.getEventType(), t -> new ArrayList<>()).add(eventHandler);
    }

    public <T extends PullRequestEvent> void unregisterEventHandler(PullRequestEventHandler<T> eventHandler) {
        eventHandlers.getOrDefault(eventHandler.getEventType(), Collections.emptyList()).remove(eventHandler);
    }

    public <T extends PullRequestEvent> void callEvent(T event) {
        List<PullRequestEventHandler<?>> handlers = eventHandlers.getOrDefault(event.getClass(),
                Collections.emptyList());

        for (PullRequestEventHandler<?> eventHandler : handlers) {
            if (eventHandler.getEventType() == event.getClass()) {
                ((PullRequestEventHandler<T>) eventHandler).handle(event);
            }
        }
    }
}
