package de.derfrzocker.sprinkler.data;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public class PullRequest implements Cloneable {

    private final PullRequestInfo info;
    private final Instant createDate;
    private final String authorId;
    private String branch;
    private Set<Rev> rev;
    private String title;
    private String description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public PullRequest clone() {
        try {
            return (PullRequest) super.clone(); // Rev list should be unmodifiable, so no need to copy it
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PullRequest that = (PullRequest) o;
        return Objects.equals(getInfo(), that.getInfo()) && Objects.equals(getCreateDate(), that.getCreateDate()) && Objects.equals(getAuthorId(), that.getAuthorId()) && Objects.equals(getBranch(), that.getBranch()) && Objects.equals(getRev(), that.getRev()) && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInfo(), getCreateDate(), getAuthorId(), getBranch(), getRev(), getTitle(), getDescription(), getStatus());
    }

    @Override
    public String toString() {
        return "PullRequest{" +
                "info=" + info +
                ", createDate=" + createDate +
                ", authorId='" + authorId + '\'' +
                ", branch='" + branch + '\'' +
                ", rev=" + rev +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
