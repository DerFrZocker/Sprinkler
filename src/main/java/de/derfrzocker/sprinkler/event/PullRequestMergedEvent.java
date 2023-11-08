package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestMergedEvent extends PullRequestEvent {

    public PullRequestMergedEvent(Repository repository, int pullRequestId, String actorId) {
        super(repository, pullRequestId, actorId);
    }
}
