package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.event.PullRequestDeletedEvent;
import de.derfrzocker.sprinkler.service.LinkService;

public class PullRequestDeletedEventHandler extends BasePullRequestEventHandler<PullRequestDeletedEvent> {

    private final LinkService linkService;

    public PullRequestDeletedEventHandler(PullRequestDao requestDao, LinkService linkService) {
        super(requestDao);
        this.linkService = linkService;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestDeletedEvent event) {
        linkService.removeLinks(pullRequest.getInfo());
        requestDao.remove(pullRequest.getInfo());
    }

    @Override
    public Class<PullRequestDeletedEvent> getEventType() {
        return PullRequestDeletedEvent.class;
    }
}
