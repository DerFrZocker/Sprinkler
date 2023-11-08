package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.event.PullRequestCommentAddedEvent;
import de.derfrzocker.sprinkler.service.LinkService;

public class PullRequestCommentAddedEventHandler extends BasePullRequestEventHandler<PullRequestCommentAddedEvent> {

    private final LinkService linkService;

    public PullRequestCommentAddedEventHandler(PullRequestDao requestDao, LinkService linkService) {
        super(requestDao);
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
