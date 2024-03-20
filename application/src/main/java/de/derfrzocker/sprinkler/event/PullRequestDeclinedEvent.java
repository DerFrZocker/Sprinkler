package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestDeclinedEvent extends PullRequestEvent {

    public PullRequestDeclinedEvent(Repository repository, int pullRequestId, String actorId) {
        super(repository, pullRequestId, actorId);
    }
}
