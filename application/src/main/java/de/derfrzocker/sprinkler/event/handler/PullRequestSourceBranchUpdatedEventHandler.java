package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.Rev;
import de.derfrzocker.sprinkler.event.PullRequestSourceBranchUpdatedEvent;
import de.derfrzocker.sprinkler.service.RevService;

import java.util.Set;

public class PullRequestSourceBranchUpdatedEventHandler
        extends BasePullRequestEventHandler<PullRequestSourceBranchUpdatedEvent> {

    private final PullRequestDao requestDao;
    private final RevService revService;

    public PullRequestSourceBranchUpdatedEventHandler(PullRequestDao requestDao, RevService revService) {
        super(requestDao);
        this.requestDao = requestDao;
        this.revService = revService;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestSourceBranchUpdatedEvent event) {
        if (!"master".equals(pullRequest.getBranch())) {
            return;
        }

        Set<Rev> revs = revService.getRev(pullRequest);
        pullRequest.setRev(revs);
        requestDao.update(pullRequest);
    }

    @Override
    public Class<PullRequestSourceBranchUpdatedEvent> getEventType() {
        return PullRequestSourceBranchUpdatedEvent.class;
    }
}
