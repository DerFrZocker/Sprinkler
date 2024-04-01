package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Very basic implementation for PullRequestDao should only be used for testing
 */
public class MemoryPullRequestDao implements PullRequestDao {

    private final Map<PullRequestInfo, PullRequest> pullRequests = new HashMap<>();

    @Override
    public Stream<PullRequest> getAll() {
        return pullRequests.values().stream();
    }

    @Override
    public Stream<PullRequest> getAll(Repository repository) {
        return pullRequests.values().stream().filter(p -> p.getInfo().repository() == repository);
    }

    @Override
    public Optional<PullRequest> get(PullRequestInfo pullRequestInfo) {
        return Optional.ofNullable(pullRequests.get(pullRequestInfo));
    }

    @Override
    public void create(PullRequest pullRequest) {
        pullRequests.put(pullRequest.getInfo(), pullRequest.clone());
    }

    @Override
    public void removeByValue(PullRequest value) {
        pullRequests.remove(value.getInfo());
    }

    @Override
    public void removeByKey(PullRequestInfo key) {
        pullRequests.remove(key);
    }

    @Override
    public void update(PullRequest pullRequest) {
        pullRequests.put(pullRequest.getInfo(), pullRequest.clone());
    }
}
