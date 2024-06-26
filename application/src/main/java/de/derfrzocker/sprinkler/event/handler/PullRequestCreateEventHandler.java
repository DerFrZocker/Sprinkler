package de.derfrzocker.sprinkler.event.handler;

import de.derfrzocker.sprinkler.dao.PullRequestDao;
import de.derfrzocker.sprinkler.data.PullRequest;
import de.derfrzocker.sprinkler.data.PullRequestInfo;
import de.derfrzocker.sprinkler.data.Rev;
import de.derfrzocker.sprinkler.data.Status;
import de.derfrzocker.sprinkler.event.PullRequestCreateEvent;
import de.derfrzocker.sprinkler.service.LinkService;
import de.derfrzocker.sprinkler.service.RevService;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class PullRequestCreateEventHandler implements PullRequestEventHandler<PullRequestCreateEvent> {

    private final PullRequestDao requestDao;
    private final RevService revService;
    private final LinkService linkService;

    public PullRequestCreateEventHandler(PullRequestDao requestDao, RevService revService, LinkService linkService) {
        this.requestDao = requestDao;
        this.revService = revService;
        this.linkService = linkService;
    }

    @Override
    public void handle(PullRequestCreateEvent event) {
        PullRequestInfo pullRequestInfo = new PullRequestInfo(event.getRepository(), event.getPullRequestId());

        Optional<PullRequest> existingPr = requestDao.get(pullRequestInfo);

        if (existingPr.isPresent()) {
            // There is already a pull request with the id.
            return;
        }

        PullRequest pullRequest = new PullRequest(pullRequestInfo, event.getCreationTime(), event.getActorId());
        pullRequest.setStatus(Status.OPENED);
        pullRequest.setTitle(event.getTitle());
        pullRequest.setBranch(event.getBranch());

        Set<Rev> revs;
        if ("master".equals(pullRequest.getBranch())) { // TODO 11/14/23 Make configurateable
            revs = revService.getRev(pullRequest);
        } else {
            revs = Collections.emptySet();
        }
        pullRequest.setRev(revs);

        linkService.searchAndCreateLink(pullRequest.getAuthorId(), pullRequest, event.getDescription());

        requestDao.create(pullRequest);
    }

    @Override
    public Class<PullRequestCreateEvent> getEventType() {
        return PullRequestCreateEvent.class;
    }
}
