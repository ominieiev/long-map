package de.comparus.opensource.longmap.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bucket<V> {
    private int hashValue;
    private List<Entity<V>> listOfValues;

    public Entity<V> findEntity(long key) {
        if (listOfValues.size() == 1 &&
                listOfValues.get(0).getKey() == key) {
            return listOfValues.get(0);
        }
        return listOfValues
                .parallelStream()
                .filter(entity -> entity.getKey() == key)
                .findAny()
                .orElse(null);
    }

    public Bucket(int hashValue) {
        this.hashValue = hashValue;
        this.listOfValues = new ArrayList<>();
    }

    public List<Entity<V>> getListOfValues() {
        return listOfValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bucket<?> bucket = (Bucket<?>) o;
        return hashValue == bucket.hashValue && Objects.equals(listOfValues, bucket.listOfValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashValue, listOfValues);
    }
}
