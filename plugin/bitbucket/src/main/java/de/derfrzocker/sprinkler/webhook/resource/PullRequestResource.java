package de.derfrzocker.sprinkler.webhook.resource;

public record PullRequestResource(int id, String title, String description, String state, boolean open, boolean closed,
                                  long createdDate, long updatedDate, RefResource toRef) {
}
