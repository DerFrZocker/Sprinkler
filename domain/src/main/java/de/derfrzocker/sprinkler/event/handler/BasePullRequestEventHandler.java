package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.ReadingDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.event.PullRequestEvent;

import java.util.Optional;

public abstract class BasePullRequestEventHandler<T extends PullRequestEvent> implements PullRequestEventHandler<T> {

    protected final ReadingDao<PullRequestInfo, PullRequest> readingPullRequestDao;

    public BasePullRequestEventHandler(ReadingDao<PullRequestInfo, PullRequest> readingDao) {
        this.readingPullRequestDao = readingDao;
    }

    @Override
    public final void handle(T event) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(event.getRepository(), event.getPullRequestId());

        Optional<PullRequest> pullRequest = readingPullRequestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        handle(pullRequest.get(), event);
    }

    public abstract void handle(PullRequest pullRequest, T event);
}
