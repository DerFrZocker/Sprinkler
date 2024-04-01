package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.ReadingDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.event.PullRequestCommentAddedEvent;
import de.derfrzocker.sprinkler.service.LinkService;

public class PullRequestCommentAddedEventHandler extends BasePullRequestEventHandler<PullRequestCommentAddedEvent> {

    private final LinkService linkService;

    public PullRequestCommentAddedEventHandler(ReadingDao<PullRequestInfo, PullRequest> readingDao, LinkService linkService) {
        super(readingDao);
        this.linkService = linkService;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestCommentAddedEvent event) {
        linkService.searchAndCreateLink(event.getActorId(), pullRequest, event.getComment());
    }

    @Override
    public Class<PullRequestCommentAddedEvent> getEventType() {
        return PullRequestCommentAddedEvent.class;
    }
}
