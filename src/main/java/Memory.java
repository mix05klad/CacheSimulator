import java.util.*;

public class Memory<K, V> implements Cache<K, V>, Iterable<V> {
    private final int capacity;
    private int hitCount;
    private int missCount;
    private final HashMap<K, Node<K, V>> map;
    private final DoublyLinkedList<K, V> list;
    private final TreeMap<Integer, ArrayList<Node<K, V>>> frequencyMap;
    private final CacheReplacementPolicy policy;


    public Memory(int capacity, CacheReplacementPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null");
        }
        this.capacity = capacity;
        this.policy = policy;
        this.map = new HashMap<>();
        this.list = new DoublyLinkedList<>();
        this.frequencyMap = policy == CacheReplacementPolicy.LFU ? new TreeMap<>() : null;
        this.hitCount = 0;
        this.missCount = 0;
    }

    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null.");
        }
        if (map.containsKey(key)) {
            hitCount++;
            Node<K, V> node = map.get(key);
            if (policy == CacheReplacementPolicy.LRU) {
                list.moveToBack(node); // Move to the back for LRU
            } else if (policy == CacheReplacementPolicy.MRU) {
                list.moveToFront(node); // Move to the front for MRU
            } else if (policy == CacheReplacementPolicy.LFU) {
                incrementFrequency(node);
            }
            return node.value;
        } else {
            missCount++;
            return null;
        }
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null.");
        }
        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value; // Update value
            if (policy == CacheReplacementPolicy.LRU) {
                list.moveToBack(node);
            } else if (policy == CacheReplacementPolicy.MRU) {
                list.moveToFront(node);
            } else if (policy == CacheReplacementPolicy.LFU) {
                incrementFrequency(node);
            }
        } else {
            if (map.size() >= capacity) {
                Node<K, V> nodeToRemove = null;
                if (policy == CacheReplacementPolicy.LRU) {
                    nodeToRemove = list.removeFirst();
                } else if (policy == CacheReplacementPolicy.MRU) {
                    nodeToRemove = list.removeLast();
                } else if (policy == CacheReplacementPolicy.LFU) {
                    nodeToRemove = evictLFU();
                }
                if (nodeToRemove != null) {
                    map.remove(nodeToRemove.key);
                }
            }
            Node<K, V> newNode = new Node<>(key, value);
            map.put(key, newNode);
            if (policy == CacheReplacementPolicy.LRU) {
                list.addLast(newNode);
            } else if (policy == CacheReplacementPolicy.MRU) {
                list.addFirst(newNode);
            } else if (policy == CacheReplacementPolicy.LFU) {
                addToFrequencyMap(newNode, 1);
            }
        }
    }

    private void incrementFrequency(Node<K, V> node) {
        int oldFreq = node.frequency;
        int newFreq = oldFreq + 1;
        node.frequency = newFreq;

        // Remove from old frequency bucket
        ArrayList<Node<K, V>> oldList = frequencyMap.get(oldFreq);
        oldList.remove(node);
        if (oldList.isEmpty()) {
            frequencyMap.remove(oldFreq);
        }

        // Add to new frequency bucket
        addToFrequencyMap(node, newFreq);
    }

    private Node<K, V> evictLFU() {
        if (frequencyMap.isEmpty()) return null;
        Map.Entry<Integer, ArrayList<Node<K, V>>> entry = frequencyMap.firstEntry();
        ArrayList<Node<K, V>> nodes = entry.getValue();
        Node<K, V> nodeToEvict = nodes.remove(0); // Remove the first node
        if (nodes.isEmpty()) {
            frequencyMap.remove(entry.getKey());
        }
        return nodeToEvict;
    }

    private void addToFrequencyMap(Node<K, V> node, int frequency) {
        frequencyMap.computeIfAbsent(frequency, k -> new ArrayList<>()).add(node);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
        list.clear();
        if (frequencyMap != null) {
            frequencyMap.clear();
        }
        hitCount = 0;
        missCount = 0;
    }

    public int getHitCount() {
        return hitCount;
    }

    public int getMissCount() {
        return missCount;
    }

    // node class for the doubly linked list
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
        int frequency;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1; // default frequency
        }
    }

    // doubly linked list class
    private static class DoublyLinkedList<K, V> {
        private Node<K, V> head;
        private Node<K, V> tail;

        DoublyLinkedList() {
            head = new Node<>(null, null);
            tail = new Node<>(null, null);
            head.next = tail;
            tail.prev = head;
        }

        void addLast(Node<K, V> node) {
            node.prev = tail.prev;
            node.next = tail;
            tail.prev.next = node;
            tail.prev = node;
        }

        void moveToBack(Node<K, V> node) {
            if (node.next == tail) return;
            remove(node);
            addLast(node);
        }

        void moveToFront(Node<K, V> node) {
            if (node.prev == head) return;
            remove(node);
            addFirst(node);
        }

        void addFirst(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        Node<K, V> removeFirst() {
            if (head.next == tail) return null;
            Node<K, V> first = head.next;
            remove(first);
            return first;
        }

        Node<K, V> removeLast() {
            if (tail.prev == head) return null;
            Node<K, V> last = tail.prev;
            remove(last);
            return last;
        }

        void remove(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        public void clear() {
            head = new Node<>(null, null);
            tail = new Node<>(null, null);
            head.next = tail;
            tail.prev = head;
        }
    }

    @Override
    public Iterator<V> iterator() {
        return new LRUCacheIterator();
    }

    private class LRUCacheIterator implements Iterator<V> {
        private Node<K, V> current;

        public LRUCacheIterator() {
            this.current = list.head.next;
        }

        @Override
        public boolean hasNext() {
            return current != null && current != list.tail;
        }

        @Override
        public V next() {
            V value = current.value;
            current = current.next;
            return value;
        }
    }
}
