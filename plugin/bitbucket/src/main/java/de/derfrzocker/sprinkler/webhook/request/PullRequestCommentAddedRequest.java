package de.derfrzocker.sprinkler.webhook.request;

import de.derfrzocker.sprinkler.webhook.resource.ActorResource;
import de.derfrzocker.sprinkler.webhook.resource.CommentResource;
import de.derfrzocker.sprinkler.webhook.resource.PullRequestResource;

public record PullRequestCommentAddedRequest(ActorResource actor, PullRequestResource pullRequest,
                                             CommentResource comment) {
}
