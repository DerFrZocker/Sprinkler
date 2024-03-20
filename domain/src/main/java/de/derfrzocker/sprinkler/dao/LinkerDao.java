package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;

import java.util.Optional;
import java.util.stream.Stream;

public interface LinkerDao {

    Stream<PullRequestLink> getAll();

    Optional<PullRequestLink> get(PullRequestInfo info);

    void remove(PullRequestLink link);

    void create(PullRequestLink link);
}
