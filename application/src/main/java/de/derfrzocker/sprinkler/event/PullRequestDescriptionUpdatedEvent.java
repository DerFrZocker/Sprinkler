package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestDescriptionUpdatedEvent extends PullRequestEvent {

    private final String description;

    public PullRequestDescriptionUpdatedEvent(Repository repository, int pullRequestId, String actorId,
                                              String description) {
        super(repository, pullRequestId, actorId);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
