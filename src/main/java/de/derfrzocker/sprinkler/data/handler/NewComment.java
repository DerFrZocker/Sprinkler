package de.derfrzocker.sprinkler.data.handler;

import de.derfrzocker.sprinkler.data.Repository;

public record NewComment(Repository repository, int id, String authorId, String message) {
}
