package de.derfrzocker.sprinkler.service;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Rev;
import de.derfrzocker.sprinkler.data.Status;
import de.derfrzocker.sprinkler.data.handler.DeletePullRequest;
import de.derfrzocker.sprinkler.data.handler.EditeComment;
import de.derfrzocker.sprinkler.data.handler.ModifiedPullRequest;
import de.derfrzocker.sprinkler.data.handler.NewComment;
import de.derfrzocker.sprinkler.data.handler.NewCommit;
import de.derfrzocker.sprinkler.data.handler.NewPullRequest;
import de.derfrzocker.sprinkler.data.handler.StatusUpdate;
import de.derfrzocker.sprinkler.handler.PullRequestHandler;
import java.util.Optional;
import java.util.Set;

public class PullRequestService implements PullRequestHandler {

    private final PullRequestDao requestDao;
    private final RevService revService;
    private final LinkService linkService;

    public PullRequestService(PullRequestDao requestDao, RevService revService, LinkService linkService) {
        this.requestDao = requestDao;
        this.revService = revService;
        this.linkService = linkService;
    }

    @Override
    public void handleNewPullRequest(NewPullRequest newPullRequest) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(newPullRequest.repository(), newPullRequest.id());
        PullRequest pullRequest = new PullRequest(pullRequestInfo, newPullRequest.creationTime(), newPullRequest.authorId());
        pullRequest.setStatus(Status.OPENED);
        pullRequest.setTitle(newPullRequest.title());
        pullRequest.setBranch(newPullRequest.branch());

        pullRequest.setRev(revService.getRev(pullRequest));
        linkService.searchAndCreateLink(pullRequest.getAuthorId(), pullRequest, newPullRequest.message());

        requestDao.create(pullRequest);
    }

    @Override
    public void handleModifiedPullRequest(ModifiedPullRequest modifiedPullRequest) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(modifiedPullRequest.repository(), modifiedPullRequest.id());
        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        boolean modified = false;

        if (!modifiedPullRequest.title().equals(pullRequest.get().getTitle())) {
            pullRequest.get().setTitle(modifiedPullRequest.title());
            modified = true;
        }

        if (modified) {
            requestDao.update(pullRequest.get());
        }
    }

    @Override
    public void handleNewComment(NewComment newComment) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(newComment.repository(), newComment.id());
        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        linkService.searchAndCreateLink(newComment.authorId(), pullRequest.get(), newComment.message());
    }

    @Override
    public void handleNewCommit(NewCommit newCommit) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(newCommit.repository(), newCommit.id());
        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        Set<Rev> revs = revService.getRev(pullRequest.get());
        pullRequest.get().setRev(revs);
        requestDao.update(pullRequest.get());
    }

    @Override
    public void handleEditeComment(EditeComment editeComment) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(editeComment.repository(), editeComment.id());
        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        linkService.searchAndCreateLink(editeComment.authorId(), pullRequest.get(), editeComment.message());
    }

    @Override
    public void handleDeletePullRequest(DeletePullRequest deletePullRequest) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(deletePullRequest.repository(), deletePullRequest.id());
        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        linkService.removeLinks(pullRequestInfo);
        requestDao.remove(pullRequestInfo);
    }

    @Override
    public void handleStatusUpdate(StatusUpdate statusUpdate) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(statusUpdate.repository(), statusUpdate.id());
        Optional<PullRequest> pullRequest = requestDao.get(pullRequestInfo);

        if (pullRequest.isEmpty()) {
            // Probably an old PR ignore
            return;
        }

        if (pullRequest.get().getStatus() == statusUpdate.status()) {
            return;
        }

        pullRequest.get().setStatus(statusUpdate.status());
        requestDao.update(pullRequest.get());
    }
}
