package de.derfrzocker.sprinkler.webhook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.derfrzocker.sprinkler.handler.PullRequestHandler;

import java.io.IOException;

public class PullRequestWebhookHandler implements HttpHandler {

    private final PullRequestHandler handler;

    public PullRequestWebhookHandler(PullRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestHeaders().getFirst("X-Request-Id");

        if (request == null) {
            return;
        }

        switch (request) {
            case "pr:opened":
                handleOpened();
                break;
            case "pr:modified":
                handleModified();
                break;
            case "pr:declined":
                handleDeclined();
                break;
        }
    }

    private void handleDeclined() {
    }

    private void handleModified() {
    }

    private void handleOpened() {

    }
}
