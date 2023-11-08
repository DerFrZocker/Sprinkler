package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.event.PullRequestTitleUpdatedEvent;

import java.util.Objects;

public class PullRequestTitleUpdatedEventHandler extends BasePullRequestEventHandler<PullRequestTitleUpdatedEvent> {

    public PullRequestTitleUpdatedEventHandler(PullRequestDao requestDao) {
        super(requestDao);
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestTitleUpdatedEvent event) {
        if (Objects.equals(pullRequest.getTitle(), event.getTitle())) {
            // Titles are the same no need to update something
            return;
        }

        pullRequest.setTitle(event.getTitle());
        requestDao.update(pullRequest);
    }

    @Override
    public Class<PullRequestTitleUpdatedEvent> getEventType() {
        return PullRequestTitleUpdatedEvent.class;
    }
}
