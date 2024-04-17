package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.event.PullRequestDescriptionUpdatedEvent;
import de.derfrzocker.sprinkler.service.LinkService;

import java.util.Objects;

public class PullRequestDescriptionUpdatedEventHandler
        extends BasePullRequestEventHandler<PullRequestDescriptionUpdatedEvent> {

    private final PullRequestDao requestDao;
    private final LinkService linkService;

    public PullRequestDescriptionUpdatedEventHandler(PullRequestDao requestDao, LinkService linkService) {
        super(requestDao);
        this.requestDao = requestDao;
        this.linkService = linkService;
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestDescriptionUpdatedEvent event) {
        if (Objects.equals(pullRequest.getDescription(), event.getDescription())) {
            // Descriptions are the same no need to update something
            return;
        }

        pullRequest.setDescription(event.getDescription());
        linkService.searchAndCreateLink(event.getActorId(), pullRequest, event.getDescription());
        requestDao.update(pullRequest);
    }

    @Override
    public Class<PullRequestDescriptionUpdatedEvent> getEventType() {
        return PullRequestDescriptionUpdatedEvent.class;
    }
}
