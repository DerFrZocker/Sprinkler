package de.derfrzocker.sprinkler.data.handler;

import de.derfrzocker.sprinkler.data.Repository;

public record EditeComment(Repository repository, int id, String authorId, String message) {
}
