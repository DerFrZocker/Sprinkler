package de.derfrzocker.sprinkler.event;

import de.derfrzocker.sprinkler.data.Repository;

import java.time.Instant;

public class PullRequestCreateEvent extends PullRequestEvent {

    private final String title;
    private final String description;
    private final String branch;
    private final Instant creationTime;

    public PullRequestCreateEvent(Repository repository, int pullRequestId, String actorId, String title,
                                  String description, String branch, Instant creationTime) {
        super(repository, pullRequestId, actorId);
        this.title = title;
        this.description = description;
        this.branch = branch;
        this.creationTime = creationTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBranch() {
        return branch;
    }

    public Instant getCreationTime() {
        return creationTime;
    }
}
