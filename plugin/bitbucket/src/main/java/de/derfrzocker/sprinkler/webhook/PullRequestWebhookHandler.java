package de.derfrzocker.sprinkler.webhook;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.derfrzocker.sprinkler.webhook.request.handler.RequestHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PullRequestWebhookHandler implements HttpHandler {

    private final Map<String, RequestHandler<?>> handlers = new HashMap<>();
    private final Gson gson;

    public PullRequestWebhookHandler() {
        this.gson = new Gson();
    }

    public void registerRequestHandler(RequestHandler<?> requestHandler) {
        handlers.put(requestHandler.getRequestKey(), requestHandler);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestHeaders().getFirst("X-Event-Key");

        if (request == null) {
            return;
        }

        // TODO: 10/30/23 Add auth check

        RequestHandler<?> requestHandler = handlers.get(request);

        if (requestHandler == null) {
            // TODO: 11/08/23 return correct status code
            return;
        }

        handleRequest(exchange, requestHandler);

        // TODO: 10/28/23 Close connection and send correct status code
    }

    private <T> void handleRequest(HttpExchange exchange, RequestHandler<T> requestHandler) {
        requestHandler.handle(
                gson.fromJson(new InputStreamReader(exchange.getRequestBody()), requestHandler.getRequestType()));
    }
}
