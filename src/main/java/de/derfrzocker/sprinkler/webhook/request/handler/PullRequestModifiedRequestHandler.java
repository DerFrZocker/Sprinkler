package de.derfrzocker.sprinkler.webhook.request.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.event.PullRequestDescriptionUptatedEvent;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.event.PullRequestSourceBranchChangedEvent;
import de.derfrzocker.sprinkler.event.PullRequestTitleUpdatedEvent;
import de.derfrzocker.sprinkler.webhook.request.BasicPullRequestRequest;

import java.util.Locale;

public class PullRequestModifiedRequestHandler implements RequestHandler<BasicPullRequestRequest> {

    private final PullRequestEventManager manager;

    public PullRequestModifiedRequestHandler(PullRequestEventManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(BasicPullRequestRequest resource) {
        Repository repository = Repository
                .valueOf(resource.pullRequest().toRef().repository().slug().toUpperCase(Locale.ROOT));
        int pullRequestId = resource.pullRequest().id();
        String actorId = resource.actor().slug();
        String title = resource.pullRequest().title();
        String description = resource.pullRequest().description();
        String branch = resource.pullRequest().toRef().displayId();
        PullRequestTitleUpdatedEvent titleEvent = new PullRequestTitleUpdatedEvent(repository,
                pullRequestId, actorId, title);
        PullRequestDescriptionUptatedEvent descriptionEvent = new PullRequestDescriptionUptatedEvent(repository,
                pullRequestId, actorId, description);
        PullRequestSourceBranchChangedEvent branchEvent = new PullRequestSourceBranchChangedEvent(repository,
                pullRequestId, actorId, branch);

        manager.callEvent(titleEvent);
        manager.callEvent(descriptionEvent);
        manager.callEvent(branchEvent);
    }

    @Override
    public String getRequestKey() {
        return "pr:modified";
    }

    @Override
    public Class<BasicPullRequestRequest> getRequestType() {
        return BasicPullRequestRequest.class;
    }
}
