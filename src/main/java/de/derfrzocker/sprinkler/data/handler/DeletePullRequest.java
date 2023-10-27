package de.derfrzocker.sprinkler.data.handler;

import de.derfrzocker.sprinkler.data.Repository;

public record DeletePullRequest(Repository repository, int id) {
}
