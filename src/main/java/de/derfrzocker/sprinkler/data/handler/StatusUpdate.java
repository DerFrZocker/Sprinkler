package de.derfrzocker.sprinkler.data.handler;

import de.derfrzocker.sprinkler.data.Repository;
import de.derfrzocker.sprinkler.data.Status;

public record StatusUpdate(Repository repository, int id, Status status) {
}
