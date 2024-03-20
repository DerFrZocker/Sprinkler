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
    public void remove(PullRequestLink link) {
        for (PullRequestInfo info : link.linked()) {
            pullRequestLinks.remove(info);
        }
    }

    @Override
    public void create(PullRequestLink link) {
        for (PullRequestInfo info : link.linked()) {
            pullRequestLinks.put(info, link);
        }
    }

}
