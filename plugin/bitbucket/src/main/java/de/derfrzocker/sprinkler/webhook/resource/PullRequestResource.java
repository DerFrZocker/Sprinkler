package de.derfrzocker.sprinkler.webhook.resource;

import java.time.Instant;

public record PullRequestResource(int id, String title, String description, String state, boolean open, boolean closed,
                                  Instant createdData, Instant updatedData, RefResource toRef) {
}
