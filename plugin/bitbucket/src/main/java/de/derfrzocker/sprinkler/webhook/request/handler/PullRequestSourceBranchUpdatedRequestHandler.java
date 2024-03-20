package de.derfrzocker.sprinkler.webhook.request.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.event.PullRequestSourceBranchUpdatedEvent;
import de.derfrzocker.sprinkler.webhook.request.BasicPullRequestRequest;

import java.util.Locale;

public class PullRequestSourceBranchUpdatedRequestHandler implements RequestHandler<BasicPullRequestRequest> {

    private final PullRequestEventManager manager;

    public PullRequestSourceBranchUpdatedRequestHandler(PullRequestEventManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(BasicPullRequestRequest resource) {
        Repository repository = Repository
                .valueOf(resource.pullRequest().toRef().repository().slug().toUpperCase(Locale.ROOT));
        int pullRequestId = resource.pullRequest().id();
        String actorId = resource.actor().slug();
        PullRequestSourceBranchUpdatedEvent event = new PullRequestSourceBranchUpdatedEvent(repository,
                pullRequestId, actorId);

        manager.callEvent(event);
    }

    @Override
    public String getRequestKey() {
        return "pr:from_ref_updated";
    }

    @Override
    public Class<BasicPullRequestRequest> getRequestType() {
        return BasicPullRequestRequest.class;
    }
}
