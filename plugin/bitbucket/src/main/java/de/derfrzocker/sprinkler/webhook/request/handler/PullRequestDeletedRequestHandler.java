package de.derfrzocker.sprinkler.webhook.request.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.event.PullRequestDeletedEvent;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.webhook.request.BasicPullRequestRequest;

import java.util.Locale;

public class PullRequestDeletedRequestHandler implements RequestHandler<BasicPullRequestRequest> {

    private final PullRequestEventManager manager;

    public PullRequestDeletedRequestHandler(PullRequestEventManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(BasicPullRequestRequest resource) {
        Repository repository = Repository
                .valueOf(resource.pullRequest().toRef().repository().slug().toUpperCase(Locale.ROOT));
        int pullRequestId = resource.pullRequest().id();
        String actorId = resource.actor().slug();
        PullRequestDeletedEvent event = new PullRequestDeletedEvent(repository,
                pullRequestId, actorId);

        manager.callEvent(event);
    }

    @Override
    public String getRequestKey() {
        return "pr:deleted";
    }

    @Override
    public Class<BasicPullRequestRequest> getRequestType() {
        return BasicPullRequestRequest.class;
    }
}
