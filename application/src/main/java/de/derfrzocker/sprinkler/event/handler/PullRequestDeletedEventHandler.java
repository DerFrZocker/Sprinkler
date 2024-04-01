package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.dao.WritingDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.PullRequestLink;
import de.derfrzocker.sprinkler.event.PullRequestDeletedEvent;
import de.derfrzocker.sprinkler.service.LinkService;

public class PullRequestDeletedEventHandler extends BasePullRequestEventHandler<PullRequestDeletedEvent> {

    private final PullRequestDao requestDao;
    private final WritingDao<PullRequestInfo, PullRequestLink> writingLinkDao;

    public PullRequestDeletedEventHandler(PullRequestDao requestDao, WritingDao<PullRequestInfo, PullRequestLink> writingLinkDao) {
        super(requestDao);
        this.requestDao = requestDao;
        this.writingLinkDao = writingLinkDao;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestDeletedEvent event) {
        writingLinkDao.removeByKey(pullRequest.getInfo());
        requestDao.removeByKey(pullRequest.getInfo());
    }

    @Override
    public Class<PullRequestDeletedEvent> getEventType() {
        return PullRequestDeletedEvent.class;
    }
}
