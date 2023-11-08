package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestDescriptionUptatedEvent extends PullRequestEvent {

    private final String description;

    public PullRequestDescriptionUptatedEvent(Repository repository, int pullRequestId, String actorId,
                                              String description) {
        super(repository, pullRequestId, actorId);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
