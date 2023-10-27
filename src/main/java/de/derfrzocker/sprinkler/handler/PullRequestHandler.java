package de.derfrzocker.sprinkler.handler;

import de.derfrzocker.sprinkler.data.handler.DeletePullRequest;
import de.derfrzocker.sprinkler.data.handler.EditeComment;
import de.derfrzocker.sprinkler.data.handler.NewComment;
import de.derfrzocker.sprinkler.data.handler.NewCommit;
import de.derfrzocker.sprinkler.data.handler.NewPullRequest;
import de.derfrzocker.sprinkler.data.handler.StatusUpdate;

public interface PullRequestHandler {

    void handleNewPullRequest(NewPullRequest newPullRequest);

    void handleNewComment(NewComment newComment);

    void handleNewCommit(NewCommit newCommit);

    void handleEditeComment(EditeComment editeComment);

    void handleDeletePullRequest(DeletePullRequest deletePullRequest);

    void handleStatusUpdate(StatusUpdate statusUpdate);
}
