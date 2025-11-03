import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int cacheSize = 100;
        int operations = 100000;
        Random random = new Random();

        // Test LRU Policy
        System.out.println("Testing LRU Policy:");
        Memory<Integer, Integer> lruCache = new Memory<>(cacheSize, CacheReplacementPolicy.LRU);
        simulateCacheOperations(lruCache, operations, random);

        // Test MRU Policy
        System.out.println("\nTesting MRU Policy:");
        Memory<Integer, Integer> mruCache = new Memory<>(cacheSize, CacheReplacementPolicy.MRU);
        simulateCacheOperations(mruCache, operations, random);

        // Test LFU Policy
        System.out.println("\nTesting LFU Policy:");
        Memory<Integer, Integer> lfuCache = new Memory<>(cacheSize, CacheReplacementPolicy.LFU);
        simulateCacheOperations(lfuCache, operations, random);
    }

    private static void simulateCacheOperations(Memory<Integer, Integer> cache, int operations, Random random) {
        for (int i = 0; i < operations; i++) {
            int key = random.nextInt(200); // simulate a range of keys
            if (random.nextDouble() < 0.8) {
                // 80% probability of accessing a key
                if (cache.get(key) == null) {
                    cache.put(key, random.nextInt(1000)); // Simulate a miss and insert the key
                }
            } else {
                // 20% probability of inserting a new key-value pair
                if (cache.get(key) == null) {
                    cache.put(key, random.nextInt(1000)); // Count as a miss
                } else {
                    // The key already exists, so update it and count as a hit
                    cache.put(key, random.nextInt(1000));
                }
            }
        }

        int hits = cache.getHitCount();
        int misses = cache.getMissCount();
        double hitRate = (hits * 100.0) / operations;
        double missRate = (misses * 100.0) / operations;

        System.out.printf("Total operations: %d\n", operations);
        System.out.printf("Cache Hits: %d\n", hits);
        System.out.printf("Cache Misses: %d\n", misses);
        System.out.printf("Hit Rate: %.2f%%\n", hitRate);
        System.out.printf("Miss Rate: %.2f%%\n", missRate);
    }
}