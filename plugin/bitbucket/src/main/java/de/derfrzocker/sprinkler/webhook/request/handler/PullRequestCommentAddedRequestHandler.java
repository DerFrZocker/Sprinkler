package de.derfrzocker.sprinkler.webhook.request.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.event.PullRequestCommentAddedEvent;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.webhook.request.PullRequestCommentAddedRequest;

import java.util.Locale;

public class PullRequestCommentAddedRequestHandler implements RequestHandler<PullRequestCommentAddedRequest> {

    private final PullRequestEventManager manager;

    public PullRequestCommentAddedRequestHandler(PullRequestEventManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(PullRequestCommentAddedRequest resource) {
        Repository repository = Repository
                .valueOf(resource.pullRequest().toRef().repository().slug().toUpperCase(Locale.ROOT));
        int pullRequestId = resource.pullRequest().id();
        String actorId = resource.actor().slug();
        String comment = resource.comment().text();
        PullRequestCommentAddedEvent event = new PullRequestCommentAddedEvent(repository,
                pullRequestId, actorId, comment);

        manager.callEvent(event);
    }

    @Override
    public String getRequestKey() {
        return "pr:comment:added";
    }

    @Override
    public Class<PullRequestCommentAddedRequest> getRequestType() {
        return PullRequestCommentAddedRequest.class;
    }
}
