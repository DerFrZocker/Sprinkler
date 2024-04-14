package de.derfrzocker.sprinkler.webhook.request.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.event.PullRequestCreateEvent;
import de.derfrzocker.sprinkler.event.PullRequestEventManager;
import de.derfrzocker.sprinkler.webhook.request.BasicPullRequestRequest;

import java.time.Instant;
import java.util.Locale;

public class PullRequestOpenedRequestHandler implements RequestHandler<BasicPullRequestRequest> {

    private final PullRequestEventManager manager;

    public PullRequestOpenedRequestHandler(PullRequestEventManager manager) {
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
        Instant creationTime = Instant.ofEpochMilli(resource.pullRequest().createdDate());
        PullRequestCreateEvent event = new PullRequestCreateEvent(repository, pullRequestId, actorId, title,
                description, branch, creationTime);

        manager.callEvent(event);
    }

    @Override
    public String getRequestKey() {
        return "pr:opened";
    }

    @Override
    public Class<BasicPullRequestRequest> getRequestType() {
        return BasicPullRequestRequest.class;
    }
}
