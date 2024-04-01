package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.Rev;
import de.derfrzocker.sprinkler.event.PullRequestSourceBranchChangedEvent;
import de.derfrzocker.sprinkler.service.RevService;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class PullRequestSourceBranchChangedEventHandler
        extends BasePullRequestEventHandler<PullRequestSourceBranchChangedEvent> {

    private final PullRequestDao requestDao;
    private final RevService revService;

    public PullRequestSourceBranchChangedEventHandler(PullRequestDao requestDao, RevService revService) {
        super(requestDao);
        this.revService = revService;
        this.requestDao = requestDao;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestSourceBranchChangedEvent event) {
        if (Objects.equals(event.getBranch(), pullRequest.getBranch())) {
            // Branches are the same no need to update something
            return;
        }

        Set<Rev> revs;
        if ("master".equals(event.getBranch())) { // TODO 11/14/23 Make configurateable
            revs = revService.getRev(pullRequest);
        } else {
            revs = Collections.emptySet();
        }

        pullRequest.setRev(revs);
        requestDao.update(pullRequest);
    }

    @Override
    public Class<PullRequestSourceBranchChangedEvent> getEventType() {
        return PullRequestSourceBranchChangedEvent.class;
    }
}
