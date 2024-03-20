package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.event.PullRequestEvent;

public interface PullRequestEventHandler<T extends PullRequestEvent> {

    void handle(T event);

    Class<T> getEventType();
}
