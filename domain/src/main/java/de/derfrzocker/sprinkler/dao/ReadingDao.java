package de.derfrzocker.sprinkler.dao;

import java.util.Optional;
import java.util.stream.Stream;

public interface ReadingDao<K, V> {

    Stream<V> getAll();

    Optional<V> get(K key);
}
