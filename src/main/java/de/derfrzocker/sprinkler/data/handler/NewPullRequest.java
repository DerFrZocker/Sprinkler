package de.derfrzocker.sprinkler.data.handler;

import de.derfrzocker.sprinkler.data.Repository;

public record NewPullRequest(Repository repository, int id, String authorId, String title, String message,
                             String branch) {
}
