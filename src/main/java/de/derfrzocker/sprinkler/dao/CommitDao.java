package de.derfrzocker.sprinkler.dao;

import de.derfrzocker.sprinkler.data.Commit;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import java.util.stream.Stream;

public interface CommitDao {

    Stream<Commit> getCommits(PullRequestInfo pullRequestInfo);
}
