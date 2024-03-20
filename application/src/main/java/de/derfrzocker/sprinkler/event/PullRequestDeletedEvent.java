package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestDeletedEvent extends PullRequestEvent {

    public PullRequestDeletedEvent(Repository repository, int pullRequestId, String actorId) {
        super(repository, pullRequestId, actorId);
    }
}
