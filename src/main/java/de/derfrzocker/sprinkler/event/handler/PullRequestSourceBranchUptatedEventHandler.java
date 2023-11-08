package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.Rev;
import de.derfrzocker.sprinkler.event.PullRequestSourceBranchUpdatedEvent;
import de.derfrzocker.sprinkler.service.RevService;

import java.util.Set;

public class PullRequestSourceBranchUptatedEventHandler extends BasePullRequestEventHandler<PullRequestSourceBranchUpdatedEvent> {

    private final RevService revService;

    public PullRequestSourceBranchUptatedEventHandler(PullRequestDao requestDao, RevService revService) {
        super(requestDao);
        this.revService = revService;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestSourceBranchUpdatedEvent event) {
        Set<Rev> revs = revService.getRev(pullRequest);
        pullRequest.setRev(revs);
        requestDao.update(pullRequest);
    }

    @Override
    public Class<PullRequestSourceBranchUpdatedEvent> getEventType() {
        return PullRequestSourceBranchUpdatedEvent.class;
    }
}

