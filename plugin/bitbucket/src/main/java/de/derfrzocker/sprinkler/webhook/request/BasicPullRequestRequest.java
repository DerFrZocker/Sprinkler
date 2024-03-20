package de.derfrzocker.sprinkler.webhook.request;

import de.derfrzocker.sprinkler.webhook.resource.ActorResource;
import de.derfrzocker.sprinkler.webhook.resource.PullRequestResource;

public record BasicPullRequestRequest(ActorResource actor, PullRequestResource pullRequest) {
}
