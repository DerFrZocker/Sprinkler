package de.derfrzocker.sprinkler.data;

import java.util.Set;

public record PullRequestLink(boolean hardLink, Set<PullRequestInfo> linked) {
}
