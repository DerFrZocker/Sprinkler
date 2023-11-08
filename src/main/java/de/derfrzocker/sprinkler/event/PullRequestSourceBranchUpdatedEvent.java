package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestSourceBranchUpdatedEvent extends PullRequestEvent {

    public PullRequestSourceBranchUpdatedEvent(Repository repository, int pullRequestId, String actorId) {
        super(repository, pullRequestId, actorId);
    }
}
