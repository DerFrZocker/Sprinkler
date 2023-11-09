package de.derfrzocker.sprinkler.webhook.request.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.event.PullRequestMergedEvent;
import de.derfrzocker.sprinkler.webhook.request.BasicPullRequestRequest;

import java.util.Locale;

public class PullRequestMergedRequestHandler implements RequestHandler<BasicPullRequestRequest> {

    private final PullRequestEventManager manager;

    public PullRequestMergedRequestHandler(PullRequestEventManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(BasicPullRequestRequest resource) {
        Repository repository = Repository
                .valueOf(resource.pullRequest().toRef().repository().slug().toUpperCase(Locale.ROOT));
        int pullRequestId = resource.pullRequest().id();
        String actorId = resource.actor().slug();
        PullRequestMergedEvent event = new PullRequestMergedEvent(repository,
                pullRequestId, actorId);

        manager.callEvent(event);
    }

    @Override
    public String getRequestKey() {
        return "pr:merged";
    }

    @Override
    public Class<BasicPullRequestRequest> getRequestType() {
        return BasicPullRequestRequest.class;
    }
}
