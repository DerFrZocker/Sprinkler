package de.derfrzocker.sprinkler.dao;

public interface WritingDao<K, V> {

    void update(V value);

    void create(V value);

    void removeByValue(V value);

    void removeByKey(K key);
}
