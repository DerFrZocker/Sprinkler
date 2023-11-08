package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestTitleUpdatedEvent extends PullRequestEvent {

    private final String title;

    public PullRequestTitleUpdatedEvent(Repository repository, int pullRequestId, String actorId, String title) {
        super(repository, pullRequestId, actorId);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
