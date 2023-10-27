package de.derfrzocker.sprinkler.data;

import java.util.Map;

public record Rev(int id, Map<Repository, String> hashes) {
}
