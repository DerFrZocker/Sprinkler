package de.derfrzocker.sprinkler.data;

import java.util.Set;

public record PullRequestLink(int linkId, Set<PullRequestInfo> linked) {
}
