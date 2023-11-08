package de.derfrzocker.sprinkler.webhook;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.derfrzocker.sprinkler.event.PullRequestCreateEvent;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

public class PullRequestWebhookHandler implements HttpHandler {

    private final PullRequestEventManager eventManager;
    private final Gson gson;

    public PullRequestWebhookHandler(PullRequestEventManager eventManager) {
        this.eventManager = eventManager;
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestHeaders().getFirst("X-Event-Key");

        if (request == null) {
            return;
        }

        // TODO: 10/30/23 Add auth check 

        switch (request) {
            case "pr:opened":
                handleOpened(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestOpened.class));
                break;
            case "pr:from_ref_updated payload":
                handleSourceUpdate(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestSourceUpdate.class));
                break;
            case "pr:modified":
                handleModified(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestModified.class));
                break;
            case "pr:merged":
                handleMerge(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestMerged.class));
                break;
            case "pr:declined":
                handleDeclined(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestDeclined.class));
                break;
            case "pr:deleted":
                handleDeleted(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestDeleted.class));
                break;
            case "pr:comment:added":
                handleCommentAdded(gson.fromJson(new InputStreamReader(exchange.getRequestBody()), PullRequestNewComment.class));
                break;
        }

        // TODO: 10/28/23 Close connection and send correct status code
    }


    private void handleOpened(PullRequestOpened opened) {
        de.derfrzocker.sprinkler.data.Repository repository = de.derfrzocker.sprinkler.data.Repository.valueOf(opened.pullRequest().toRef().repository().slug().toUpperCase());
        PullRequestCreateEvent pullRequestCreateEvent = new PullRequestCreateEvent(repository, opened.pullRequest().id(), opened.actor().slug(), opened.pullRequest().title(), opened.pullRequest().description(), opened.pullRequest().toRef().displayId(), opened.pullRequest().createdData());

        eventManager.callEvent(pullRequestCreateEvent);
    }

    private void handleSourceUpdate(PullRequestSourceUpdate pullRequestSourceUpdate) {
    }

    private void handleModified(PullRequestModified pullRequestModified) {
    }

    private void handleMerge(PullRequestMerged pullRequestMerged) {
    }

    private void handleDeclined(PullRequestDeclined pullRequestDeclined) {
    }

    private void handleDeleted(PullRequestDeleted pullRequestDeleted) {
    }

    private void handleCommentAdded(PullRequestNewComment pullRequestNewComment) {
    }

    public record PullRequestOpened(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequestSourceUpdate(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequestModified(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequestNewComment(Actor actor, PullRequest pullRequest, Comment comment) {
    }

    public record PullRequestMerged(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequestDeleted(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequestDeclined(Actor actor, PullRequest pullRequest) {
    }

    public record PullRequest(int id, String title, String description, String state, boolean open, boolean closed,
                              Instant createdData, Instant updatedData, Ref toRef) {
    }

    public record Actor(String name, int id, String slug) {
    }

    public record Ref(String id, String displayId, Repository repository) {
    }

    public record Repository(String slug) {
    }

    public record Comment(String text) {
    }
}
