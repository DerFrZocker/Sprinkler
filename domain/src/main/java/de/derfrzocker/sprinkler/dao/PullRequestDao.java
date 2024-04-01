package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Repository;

import java.util.stream.Stream;

public interface PullRequestDao extends ReadingDao<PullRequestInfo, PullRequest>, WritingDao<PullRequestInfo, PullRequest> {

    Stream<PullRequest> getAll(Repository repository);
}
