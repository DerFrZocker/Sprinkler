package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;

import java.util.Optional;
import java.util.stream.Stream;

public interface LinkerDao {

    Stream<PullRequestLink> getAll();

    Stream<PullRequestLink> getAll(PullRequestInfo requestInfo);

    Optional<PullRequestLink> get(int linkId);

    void remove(int linkId);

    void create(PullRequestLink link);
}
