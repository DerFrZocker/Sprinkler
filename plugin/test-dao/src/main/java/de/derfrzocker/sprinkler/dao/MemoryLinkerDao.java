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
        System.out.println("Getting all links");
        return pullRequestLinks.values().stream();
    }

    @Override
    public Optional<PullRequestLink> get(PullRequestInfo info) {
        System.out.println("Getting link " + info);
        return Optional.ofNullable(pullRequestLinks.get(info));
    }

    @Override
    public void removeByValue(PullRequestLink link) {
        System.out.println("Removing link by value " + link);
        for (PullRequestInfo info : link.linked()) {
            pullRequestLinks.remove(info);
        }
    }

    @Override
    public void removeByKey(PullRequestInfo key) {
        System.out.println("Removing link by key" + key);
        get(key).ifPresent(this::removeByValue);
    }

    @Override
    public void update(PullRequestLink value) {
        System.out.println("Updating link " + value);
        removeByValue(value);
        create(value);
    }

    @Override
    public void create(PullRequestLink link) {
        System.out.println("Creating link " + link);
        for (PullRequestInfo info : link.linked()) {
            pullRequestLinks.put(info, link);
        }
    }

}
