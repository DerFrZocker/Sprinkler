package de.derfrzocker.sprinkler.webhook.request.handler;

public interface RequestHandler<T> {

    void handle(T resource);

    String getRequestKey();

    Class<T> getRequestType();
}
