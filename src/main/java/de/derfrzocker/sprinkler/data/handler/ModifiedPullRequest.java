package de.derfrzocker.sprinkler.data.handler;

import de.derfrzocker.sprinkler.data.Repository;

public record ModifiedPullRequest(Repository repository, int id, String message, String title) {
}
