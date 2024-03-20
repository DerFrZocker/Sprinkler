package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.event.PullRequestEvent;

import java.util.Optional;

public abstract class BasePullRequestEventHandler<T extends PullRequestEvent> implements PullRequestEventHandler<T> {

    protected final PullRequestDao requestDao;

    public BasePullRequestEventHandler(PullRequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @Override
    public final void handle(T event) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(event.getRepository(), event.getPullRequestId());

        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        handle(pullRequest.get(), event);
    }

    public abstract void handle(PullRequest pullRequest, T event);
}
