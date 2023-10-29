package de.derfrzocker.sprinkler.data;

import java.util.Set;

public record PullRequestLink(Set<PullRequestInfo> linked) {
}
