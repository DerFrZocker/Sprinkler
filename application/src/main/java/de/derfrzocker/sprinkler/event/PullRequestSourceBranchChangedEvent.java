package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestSourceBranchChangedEvent extends PullRequestEvent {

    private final String branch;

    public PullRequestSourceBranchChangedEvent(Repository repository, int pullRequestId, String actorId,
                                               String branch) {
        super(repository, pullRequestId, actorId);
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }
}
