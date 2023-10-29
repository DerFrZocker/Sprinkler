package de.derfrzocker.sprinkler.webhook;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.derfrzocker.sprinkler.data.handler.NewPullRequest;
import de.derfrzocker.sprinkler.handler.PullRequestHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

public class PullRequestWebhookHandler implements HttpHandler {

    private final PullRequestHandler handler;
    private final Gson gson;

    public PullRequestWebhookHandler(PullRequestHandler handler) {
        this.handler = handler;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestHeaders().getFirst("X-Event-Key");

        if (request == null) {
            return;
        }

        switch (request) {
            case "pr:opened":
                handleOpened(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestOpened.class));
                break;
            case "pr:modified":
                handleModified();
                break;
            case "pr:declined":
                handleDeclined();
                break;
        }

        // TODO: 10/28/23 Close connection and send correct status code
    }

    private void handleDeclined() {
    }

    private void handleModified() {
    }

    private void handleOpened(PullRequestOpened opened) {
        de.derfrzocker.sprinkler.data.Repository repository = de.derfrzocker.sprinkler.data.Repository.valueOf(opened.pullRequest().toRef().repository().slug().toUpperCase());
        NewPullRequest newPullRequest = new NewPullRequest(repository, opened.pullRequest().id(), opened.actor().slug(), opened.pullRequest().title(), opened.pullRequest().description(), opened.pullRequest().toRef().displayId(), opened.pullRequest().createdData());

        handler.handleNewPullRequest(newPullRequest);
    }

    public record PullRequestNewComment() {
    }

    public record PullRequestOpened(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequest(int id, String title, String description, String state, boolean open, boolean closed, Instant createdData, Instant updatedData, Ref toRef) {
    }

    public record Actor(String name, int id, String slug) {
    }

    public record Ref(String id, String displayId, Repository repository) {
    }

    public record Repository(String slug) {
    }
}
