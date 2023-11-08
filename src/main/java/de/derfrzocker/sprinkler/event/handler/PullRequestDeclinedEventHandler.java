package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.Status;
import de.derfrzocker.sprinkler.event.PullRequestDeclinedEvent;

import java.util.Objects;

public class PullRequestDeclinedEventHandler extends BasePullRequestEventHandler<PullRequestDeclinedEvent> {

    public PullRequestDeclinedEventHandler(PullRequestDao requestDao) {
        super(requestDao);
    }

    @Override
    public void handle(PullRequest pullRequest, PullRequestDeclinedEvent event) {
        if (Objects.equals(Status.CLOSED, pullRequest.getStatus())) {
            // Statuses are the same no need to update something
            return;
        }

        pullRequest.setStatus(Status.CLOSED);
        requestDao.update(pullRequest);
    }

    @Override
    public Class<PullRequestDeclinedEvent> getEventType() {
        return PullRequestDeclinedEvent.class;
    }
}
