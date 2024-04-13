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
    private final String token;
    private final Gson gson;

    public PullRequestWebhookHandler(String token, Gson gson) {
        this.token = format(token);
        this.gson = gson;
    }

    public void registerRequestHandler(RequestHandler<?> requestHandler) {
        handlers.put(requestHandler.getRequestKey(), requestHandler);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String request = exchange.getRequestHeaders().getFirst("X-Event-Key");

            if (request == null || request.isBlank()) {
                close(exchange, 400); // Bad Request
                return;
            }

            String requestToken = format(exchange.getRequestHeaders().getFirst("Authorization"));

            if (!token.equals(requestToken)) {
                close(exchange, 401); // Unauthorized
                return;
            }

            RequestHandler<?> requestHandler = handlers.get(request);

            if (requestHandler == null) {
                close(exchange, 400); // Bad Request
                return;
            }

            handleRequest(exchange, requestHandler);

            close(exchange, 200);
        } catch (Exception e) {
            e.printStackTrace();
            close(exchange, 500);
            throw e;
        }
    }

    private static String format(String token) {
        if (token == null) {
            return "";
        }

        return token.trim();
    }

    private <T> void handleRequest(HttpExchange exchange, RequestHandler<T> requestHandler) {
        requestHandler.handle(
                gson.fromJson(new InputStreamReader(exchange.getRequestBody()), requestHandler.getRequestType()));
    }

    private void close(HttpExchange exchange, int code) throws IOException {
        exchange.getRequestBody().close();
        exchange.sendResponseHeaders(code, -1);
        exchange.getResponseBody().close();
    }
}
