package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestEvent {

    private final Repository repository;
    private final int pullRequestId;
    private final String actorId;

    public PullRequestEvent(Repository repository, int pullRequestId, String actorId) {
        this.repository = repository;
        this.pullRequestId = pullRequestId;
        this.actorId = actorId;
    }

    public Repository getRepository() {
        return repository;
    }

    public int getPullRequestId() {
        return pullRequestId;
    }

    public String getActorId() {
        return actorId;
    }
}
