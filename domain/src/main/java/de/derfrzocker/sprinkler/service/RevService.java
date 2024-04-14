package de.derfrzocker.sprinkler.service;

import de.derfrzocker.sprinkler.dao.CommitDao;
import de.derfrzocker.sprinkler.dao.RevDao;
import de.derfrzocker.sprinkler.data.Commit;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.Rev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        // TODO: 11/7/23 Add branch 
        Stream<Commit> commitStream = commitDao.getCommits(pullRequest.getInfo());

        return commitStream
                .map(Commit::hashes)
                .map(this::reverse) // Reverse since the target repo commit hash is usually first
                .flatMap(Collection::stream)
                .map(hash -> revDao.get(pullRequest.getInfo().repository(), hash))
                .filter(revs -> !revs.isEmpty())
                .findFirst()
                .orElse(new HashSet<>());
    }

    private List<String> reverse(List<String> list) {
        List<String> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }
}
