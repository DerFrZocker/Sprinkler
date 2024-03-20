package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

public class PullRequestCommentAddedEvent extends PullRequestEvent {

    private final String comment;

    public PullRequestCommentAddedEvent(Repository repository, int pullRequestId, String actorId, String comment) {
        super(repository, pullRequestId, actorId);
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
