package de.comparus.opensource.longmap.inner;

import java.util.Objects;

public class Entity<V> {
    private long key;
    private V value;

    public Entity(long key, V value) {
        this.key = key;
        this.value = value;
    }

    public long getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return key == entity.key && Objects.equals(value, entity.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
