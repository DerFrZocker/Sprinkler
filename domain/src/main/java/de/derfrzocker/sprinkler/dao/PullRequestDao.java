package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface PullRequestDao {

    Stream<PullRequest> getAll();

    Stream<PullRequest> getAll(Repository repository);

    Optional<PullRequest> get(PullRequestInfo pullRequestInfo);

    void remove(PullRequestInfo pullRequestInfo);

    void create(PullRequest pullRequest);

    void update(PullRequest pullRequest);
}
