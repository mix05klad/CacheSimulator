import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.Random;

class MemoryTests {

    @Test
    void testBasicPutAndGet() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LRU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        assertEquals("one", cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testEvictionOnCapacityExceeded() {
        Memory<Integer, String> cache = new Memory<>(2, CacheReplacementPolicy.LRU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three"); // evicts key 1

        assertNull(cache.get(1)); // 1 should have been evicted
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testUpdateValue() {
        Memory<Integer, String> cache = new Memory<>(2, CacheReplacementPolicy.LRU);

        cache.put(1, "one");
        cache.put(1, "updated-one"); // update value of key 1
        assertEquals("updated-one", cache.get(1));

        cache.put(2, "two");
        cache.put(3, "three"); // evicts key 1

        assertNull(cache.get(1)); // 1 should have been evicted
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testAccessOrder() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LRU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        cache.get(1); //access 1 to make it MRU
        cache.put(4, "four"); //evicts key 2 (LRU)

        assertNull(cache.get(2)); //2 should have been evicted
        assertEquals("one", cache.get(1));
        assertEquals("three", cache.get(3));
        assertEquals("four", cache.get(4));
    }

    @Test
    void testIteratorOrder() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LRU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        //access 1 to make it MRU
        cache.get(1);

        Iterator<String> iterator = cache.iterator();

        //check the order
        assertTrue(iterator.hasNext());
        assertEquals("two", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("three", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("one", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testCapacityOne() {
        Memory<Integer, String> cache = new Memory<>(1, CacheReplacementPolicy.LRU);

        cache.put(1, "one");
        assertEquals("one", cache.get(1));

        cache.put(2, "two"); //evicts key 1
        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));
    }

    @Test
    void testNullValues() {
        Memory<Integer, String> cache = new Memory<>(2, CacheReplacementPolicy.LRU);

        cache.put(1, null); //allow null values
        assertNull(cache.get(1));

        cache.put(2, "two");
        cache.put(3, "three"); //evicts key 1 (LRU)

        assertNull(cache.get(1)); // 1 should have been evicted
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testNonExistentKey() {
        Memory<Integer, String> cache = new Memory<>(2, CacheReplacementPolicy.LRU);

        assertNull(cache.get(42)); //key 42 does not exist
    }

    @Test
    void testStressTest() {
        final int capacity = 10_000;
        Memory<Integer, Integer> cache = new Memory<>(capacity, CacheReplacementPolicy.LRU);

        for (int i = 0; i < 20_000; i++) {
            cache.put(i, i); //add twice the capacity to ensure eviction
        }

        for (int i = 0; i < 10_000; i++) {
            assertNull(cache.get(i)); //first 10,000 should have been evicted
        }

        for (int i = 10_000; i < 20_000; i++) {
            assertEquals(i, cache.get(i)); //second 10,000 should still be in the cache
        }
    }

    @Test
    void testSequentialAccess() {
        Memory<Integer, Integer> cache = new Memory<>(100, CacheReplacementPolicy.LRU);

        Random rand = new Random();
        for (int i = 0; i < 10000; i++) {
            int key = rand.nextInt(200); //random keys between 0 and 199
            cache.put(key, key);
            cache.get(rand.nextInt(200));
        }

        assertTrue(cache.size() <= 100);
    }

    @Test
    public void testSizeAndClear() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LRU);

        //add elements to the cache
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        //verify size
        assertEquals(3, cache.size());

        //clear the cache and verify
        cache.clear();
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
    }

    @Test
    void testBasicPutAndGetMRU() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.MRU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        assertEquals("one", cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testCapacityOneMRU() {
        Memory<Integer, String> cache = new Memory<>(1, CacheReplacementPolicy.MRU);

        cache.put(1, "one");
        assertEquals("one", cache.get(1));

        cache.put(2, "two"); //evicts key 1 (MRU)
        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));
    }

    @Test
    void testNonExistentKeyMRU() {
        Memory<Integer, String> cache = new Memory<>(2, CacheReplacementPolicy.MRU);

        assertNull(cache.get(42)); //key 42 does not exist
    }

    @Test
    void testStressTestMRU() {
        final int capacity = 10_000;
        Memory<Integer, Integer> cache = new Memory<>(capacity, CacheReplacementPolicy.MRU);

        for (int i = 0; i < 20_000; i++) {
            cache.put(i, i); //add twice the capacity to ensure eviction
        }

        for (int i = 0; i < 10_000; i++) {
            assertNull(cache.get(i)); //first 10,000 should have been evicted
        }

        for (int i = 10_000; i < 20_000; i++) {
            assertEquals(i, cache.get(i)); //second 10,000 should still be in the cache
        }
    }

    @Test
    public void testSizeAndClearMRU() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.MRU);

        //add elements to the cache
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        //verify size
        assertEquals(3, cache.size());

        //clear the cache and verify
        cache.clear();
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
    }

    @Test
    void testEdgeCaseSingleElementMRU() {
        Memory<Integer, String> cache = new Memory<>(1, CacheReplacementPolicy.MRU);

        cache.put(1, "one");
        assertEquals("one", cache.get(1));

        cache.put(2, "two"); //evicts key 1
        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));

        cache.get(2); //access key 2 to confirm it's MRU
        cache.put(3, "three"); //evicts key 2
        assertNull(cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testMRUAfterClear() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.MRU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        //clear the cache
        cache.clear();

        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());

        // add new elements after clearing
        cache.put(4, "four");
        cache.put(5, "five");

        assertEquals("four", cache.get(4));
        assertEquals("five", cache.get(5));
    }

    @Test
    void testBasicPutAndGetLFU() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        assertEquals("one", cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testEvictionLFU() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        // access key 1 twice, making it the most frequently used
        cache.get(1);
        cache.get(1);

        //access key 2 once
        cache.get(2);

        // add a new key, which should evict key 3 (least frequently used)
        cache.put(4, "four");

        assertEquals("one", cache.get(1)); // still present
        assertEquals("two", cache.get(2)); //still present
        assertNull(cache.get(3)); //evicted
        assertEquals("four", cache.get(4)); // newly added
    }

    @Test
    void testTieBreakingLFU() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        //access all keys once (tie in frequency)
        cache.get(1);
        cache.get(2);
        cache.get(3);

        // add a new key, should evict the oldest key (1)
        cache.put(4, "four");

        assertNull(cache.get(1)); //evicted
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
        assertEquals("four", cache.get(4));
    }

    @Test
    void testEdgeCaseSingleElementLFU() {
        Memory<Integer, String> cache = new Memory<>(1, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        assertEquals("one", cache.get(1));

        cache.put(2, "two"); //evicts key 1
        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));
    }

    @Test
    void testCapacityOneLFU() {
        Memory<Integer, String> cache = new Memory<>(1, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        assertEquals("one", cache.get(1));

        cache.put(2, "two"); // evicts key 1
        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));

        cache.get(2); //access key 2 to confirm it's LFU
        cache.put(3, "three"); // evicts key 2
        assertNull(cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    void testLFUAfterClear() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        // clear the cache
        cache.clear();

        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());

        //add new elements after clearing
        cache.put(4, "four");
        cache.put(5, "five");

        assertEquals("four", cache.get(4));
        assertEquals("five", cache.get(5));
    }

    @Test
    void testStressTestLFU() {
        final int capacity = 10_000;
        Memory<Integer, Integer> cache = new Memory<>(capacity, CacheReplacementPolicy.LFU);

        for (int i = 0; i < 20_000; i++) {
            cache.put(i, i); // add twice the capacity to ensure eviction
        }

        for (int i = 0; i < 10_000; i++) {
            assertNull(cache.get(i)); //first 10,000 should have been evicted
        }

        for (int i = 10_000; i < 20_000; i++) {
            assertEquals(i, cache.get(i)); // second 10,000 should still be in the cache
        }
    }

    @Test
    void testAccessOrderLFU() {
        Memory<Integer, String> cache = new Memory<>(3, CacheReplacementPolicy.LFU);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");

        cache.get(1); // access key 1 twice
        cache.get(1);

        cache.get(2); // access key 2 once

        cache.put(4, "four"); // should evict key 3 (least frequently used)

        assertEquals("one", cache.get(1)); // most frequently used
        assertEquals("two", cache.get(2)); // still present
        assertNull(cache.get(3)); // evicted
        assertEquals("four", cache.get(4)); // newly added
    }
}