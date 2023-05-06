package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.inner.Bucket;
import de.comparus.opensource.longmap.inner.Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of Map for objects with key type long.
  * Map is not null safety. If elements not fount result is null.
 */
@SuppressWarnings("unchecked")

public class LongMapImpl<V> implements LongMap<V> {
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final int RATIO_OF_INCREASING = 2;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final float ARRAY_LIST_MAX_COUNT = 0x7fffffff;

    private long size;
    private int bucketsCount;
    private float loadFactor;
    private List<Bucket> buckets;

    public LongMapImpl() {
        this.size = 0;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.bucketsCount = DEFAULT_INITIAL_CAPACITY;
        this.buckets = new ArrayList<>();
        IntStream
                .range(0, bucketsCount)
                .forEach(hashId -> buckets.add(new Bucket(hashId)));
    }

    public LongMapImpl(int initialBucketsCount, float loadFactor) {
        this.bucketsCount = initialBucketsCount;
        this.loadFactor = loadFactor;
    }

    @Override
    public V put(long key, V value) {
        if (needsToResize()) {
            resize();
        }

        Bucket bucket = getBucketByKey(key);
        Entity<V> entity = bucket.findEntity(key);
        if (entity == null) {
            bucket.getListOfValues().add(new Entity<V>(key, value));
            size++;
            //System.out.println("added key " + key + "; size is " + size);
            return null;
        } else {
            V oldValue = entity.getValue();
            entity.setValue(value);
            return oldValue;
        }
    }

    @Override
    public V get(long key) {
        Bucket<V> bucket = getBucketByKey(key);
        Entity<V> entity = bucket.findEntity(key);
        return (entity == null) ? null : entity.getValue();
    }

    @Override
    public V remove(long key) {
        Bucket<V> bucket = getBucketByKey(key);
        Entity<V> entity = bucket.findEntity(key);
        V oldValue = entity.getValue();
        bucket.getListOfValues().remove(entity);
        size--;
        return oldValue;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return buckets
                .parallelStream()
                .flatMap(bucket -> bucket.getListOfValues().stream())
                .filter(entity -> ((Entity<V>) entity).getValue().equals(value))
                .findAny()
                .isPresent();
    }

    @Override
    public long[] keys() {
        return buckets
                .parallelStream()
                .flatMap(bucket -> bucket.getListOfValues().stream())
                .mapToLong(entity -> ((Entity) entity).getKey())
                .toArray();
    }

    @Override
    public V[] values() {

        List<V> values = (List<V>) buckets
                .parallelStream()
                .flatMap(bucket -> bucket.getListOfValues().stream())
                .map(entity -> ((Entity<V>) entity).getValue())
                .collect(Collectors.toList());
        if (values.isEmpty()) {
            return null;
        }

        V[] result = (V[]) Array.newInstance(values.get(0).getClass(), values.size());
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        buckets
                .parallelStream()
                .forEach(bucket -> bucket.getListOfValues().clear());
        size = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongMapImpl<?> aNew = (LongMapImpl<?>) o;
        return size == aNew.size && bucketsCount == aNew.bucketsCount && Float.compare(aNew.loadFactor, loadFactor) == 0
                && Objects.equals(buckets, aNew.buckets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, bucketsCount, loadFactor, buckets);
    }

    private Bucket getBucketByKey(long key) {
        int bucketHash = getBucketHash(key);
        return buckets.get(bucketHash);
    }

    private int getBucketHash(long key) {
        return Math.abs(Long.hashCode(key)) % bucketsCount;
    }

    private boolean needsToResize() {
        return ((float) size / bucketsCount > loadFactor) &&
                (bucketsCount * RATIO_OF_INCREASING < MAXIMUM_CAPACITY);
    }

    private void resize() {
        this.bucketsCount = bucketsCount * RATIO_OF_INCREASING;
        List<Entity> oldEntity = (List<Entity>) buckets
                .stream()
                .flatMap(bucket -> bucket.getListOfValues().stream())
                .collect(Collectors.toList());
        this.buckets = new ArrayList<>();
        this.size = 0;
        IntStream
                .range(0, bucketsCount)
                .forEach(intHashValue -> buckets.add(new Bucket<>(intHashValue)));
        oldEntity
                .forEach(entity -> put(entity.getKey(), (V) entity.getValue()));
    }


}

