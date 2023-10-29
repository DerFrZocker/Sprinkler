package de.derfrzocker.sprinkler.service;

import de.derfrzocker.sprinkler.dao.CommitDao;
import de.derfrzocker.sprinkler.dao.RevDao;
import de.derfrzocker.sprinkler.data.Commit;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.Rev;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class RevService {

    private final RevDao revDao;
    private final CommitDao commitDao;

    public RevService(RevDao revDao, CommitDao commitDao) {
        this.revDao = revDao;
        this.commitDao = commitDao;
    }

    public Set<Rev> getRev(PullRequest pullRequest) {
        Stream<Commit> commitStream = commitDao.getCommits(pullRequest.getInfo());

        return commitStream
                .map(Commit::hashes)
                .flatMap(Collection::stream)
                .map(hash -> revDao.get(pullRequest.getInfo().repository(), hash))
                .filter(revs -> !revs.isEmpty())
                .findFirst()
                .orElse(new HashSet<>());
    }
}
