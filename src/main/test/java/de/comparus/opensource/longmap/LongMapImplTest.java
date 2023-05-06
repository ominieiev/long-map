package de.comparus.opensource.longmap;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;

public class LongMapImplTest {
    private LongMap map;
    private static String TEST_ARRAY_OF_VALUES[] = new String[]{
            "Test1", "Test2", "Test3", "Test4"};
    private static long TEST_ARRAY_OF_KEYS[] = {
            Long.MIN_VALUE, Long.MAX_VALUE, -1, 1};
    private Integer TEST_ENTYTY_COUNT = 4;
    private static long ADDITIONAL_TEST_KEY = 0;
    private static String ADDITIONAL_TEST_VALUE = "Test0";

    void init() {
        map = new LongMapImpl();
        for (int i = 0; i < TEST_ENTYTY_COUNT; i++) {
            map.put(TEST_ARRAY_OF_KEYS[i], TEST_ARRAY_OF_VALUES[i]);
        }
    }

    @Test
    public void putAndGet() {
        init();
        for (int i = 0; i < TEST_ENTYTY_COUNT; i++) {
            assertEquals(TEST_ARRAY_OF_VALUES[i], map.get(TEST_ARRAY_OF_KEYS[i]));
        }
    }

    @Test
    public void putAndRemove() {
        init();
        for (int i = 0; i < TEST_ENTYTY_COUNT; i++) {
            map.remove(TEST_ARRAY_OF_KEYS[i]);
            assertEquals(null, map.get(TEST_ARRAY_OF_KEYS[i]));
        }
    }

    @Test
    public void isEmpty() {
        init();
        assertEquals(false, map.isEmpty());
        for (int i = 0; i < TEST_ENTYTY_COUNT; i++) {
            map.remove(TEST_ARRAY_OF_KEYS[i]);
        }
        assertEquals(true, map.isEmpty());
    }

    @Test
    public void containsKey() {
        map = new LongMapImpl();
        assertEquals(false, map.containsKey(TEST_ARRAY_OF_KEYS[0]));
        init();
        assertEquals(true, map.containsKey(TEST_ARRAY_OF_KEYS[0]));
        map.remove(TEST_ARRAY_OF_KEYS[0]);
        assertEquals(false, map.containsKey(TEST_ARRAY_OF_KEYS[0]));
    }

    @Test
    public void containsValue() {
        map = new LongMapImpl();
        assertEquals(false, map.containsValue(TEST_ARRAY_OF_VALUES[0]));
        init();
        assertEquals(true, map.containsValue(TEST_ARRAY_OF_VALUES[0]));
        map.remove(TEST_ARRAY_OF_KEYS[0]);
        assertEquals(false, map.containsValue(TEST_ARRAY_OF_VALUES[0]));
    }

    @Test
    public void keys() {
        map = new LongMapImpl();
        assertFalse(Arrays.equals(TEST_ARRAY_OF_KEYS, map.keys()));
        init();
        assertTrue(Arrays.equals(TEST_ARRAY_OF_KEYS, map.keys()));
        map.put(ADDITIONAL_TEST_KEY, ADDITIONAL_TEST_VALUE);
        assertFalse(Arrays.equals(TEST_ARRAY_OF_KEYS, map.keys()));
    }

    @Test
    public void values() {
        map = new LongMapImpl();
        assertFalse(Arrays.equals(TEST_ARRAY_OF_VALUES, map.values()));
        init();
        assertTrue(Arrays.equals(TEST_ARRAY_OF_VALUES, map.values()));
        map.put(ADDITIONAL_TEST_KEY, ADDITIONAL_TEST_VALUE);
        assertFalse(Arrays.equals(TEST_ARRAY_OF_VALUES, map.values()));
    }

    @Test
    public void size() {
        map = new LongMapImpl();
        assertEquals(0, map.size());
        init();
        assertEquals((long) TEST_ENTYTY_COUNT, map.size());
        map.put(ADDITIONAL_TEST_KEY, ADDITIONAL_TEST_VALUE);
        assertEquals((long) TEST_ENTYTY_COUNT + 1, map.size());
        map.remove(ADDITIONAL_TEST_KEY);
        assertEquals((long) TEST_ENTYTY_COUNT , map.size());
    }

    @Test
    public void clear() {
        init();
        map.clear();
        assertEquals(0, map.size());
        assertEquals(null, map.get(TEST_ARRAY_OF_KEYS[0]));
    }
}