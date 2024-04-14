package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Very basic implementation for PullRequestDao should only be used for testing
 */
public class MemoryLinkerDao implements LinkerDao {

    private final Map<PullRequestInfo, PullRequestLink> pullRequestLinks = new HashMap<>();

    @Override
    public Stream<PullRequestLink> getAll() {
        return pullRequestLinks.values().stream();
    }

    @Override
    public Optional<PullRequestLink> get(PullRequestInfo info) {
        return Optional.ofNullable(pullRequestLinks.get(info));
    }

    @Override
    public void removeByValue(PullRequestLink link) {
        for (PullRequestInfo info : link.linked()) {
            pullRequestLinks.remove(info);
        }
    }

    @Override
    public void removeByKey(PullRequestInfo key) {
        get(key).ifPresent(this::removeByValue);
    }

    @Override
    public void update(PullRequestLink value) {
        removeByValue(value);
        create(value);
    }

    @Override
    public void create(PullRequestLink link) {
        for (PullRequestInfo info : link.linked()) {
            pullRequestLinks.put(info, link);
        }
    }

}
