package de.derfrzocker.sprinkler.data;

import java.time.Instant;
import java.util.Set;

public class PullRequest {

    private final PullRequestInfo info;
    private final Instant createDate;
    private final String authorId;
    private String branch;
    private Set<Rev> rev;
    private String title;
    private Status status;

    public PullRequest(PullRequestInfo info, Instant createDate, String authorId) {
        this.info = info;
        this.createDate = createDate;
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Set<Rev> getRev() {
        return rev;
    }

    public void setRev(Set<Rev> rev) {
        this.rev = rev;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PullRequestInfo getInfo() {
        return info;
    }

    public Instant getCreateDate() {
        return createDate;
    }
}
