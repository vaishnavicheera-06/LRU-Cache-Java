import java.util.HashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) Cache Implementation
 *
 * Data structures used:
 *   - HashMap<Integer, Node>  → O(1) key lookup
 *   - Circular Doubly Linked List → O(1) order tracking
 *
 * Time Complexity  : O(1) for both get() and put()
 * Space Complexity : O(capacity)
 *
 * List layout:
 *   HEAD (dummy) ↔ [MRU node] ↔ ... ↔ [LRU node] ↔ TAIL (dummy)
 */
public class LRUCache {

    // ─────────────────────────────────────────────
    // Inner Node class
    // ─────────────────────────────────────────────
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key   = key;
            this.value = value;
        }
    }

    // ─────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────
    private final int              capacity;
    private final Map<Integer, Node> map;
    private final Node             head; // dummy head (MRU side)
    private final Node             tail; // dummy tail (LRU side)

    // ─────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.map      = new HashMap<>();

        // Sentinel nodes — never hold real data
        head      = new Node(0, 0);
        tail      = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    // ─────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────

    /**
     * Returns the value for the given key, or -1 if not present.
     * Moves the accessed node to the MRU position.
     * Time: O(1)
     */
    public int get(int key) {
        Node node = map.get(key);
        if (node == null) {
            return -1; // cache miss
        }
        // Move to front → mark as most recently used
        removeNode(node);
        insertAtFront(node);
        return node.value;
    }

    /**
     * Inserts or updates a key-value pair.
     * If capacity is exceeded, evicts the least recently used entry.
     * Time: O(1)
     */
    public void put(int key, int value) {
        Node existing = map.get(key);

        if (existing != null) {
            // Update value and move to front
            existing.value = value;
            removeNode(existing);
            insertAtFront(existing);
            return;
        }

        // Evict LRU if at capacity
        if (map.size() == capacity) {
            Node lru = tail.prev; // LRU is always just before tail
            removeNode(lru);
            map.remove(lru.key);
        }

        // Insert new node at front
        Node newNode = new Node(key, value);
        insertAtFront(newNode);
        map.put(key, newNode);
    }

    /**
     * Returns the current number of entries in the cache.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns true if the cache contains the given key (without updating order).
     */
    public boolean containsKey(int key) {
        return map.containsKey(key);
    }

    /**
     * Removes all entries from the cache.
     */
    public void clear() {
        map.clear();
        head.next = tail;
        tail.prev = head;
    }

    // ─────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────

    /**
     * Detaches a node from the doubly linked list.
     * Rewires its neighbours to point to each other.
     */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /**
     * Inserts a node immediately after the head sentinel (= MRU position).
     */
    private void insertAtFront(Node node) {
        node.next      = head.next;
        node.prev      = head;
        head.next.prev = node;
        head.next      = node;
    }

    // ─────────────────────────────────────────────
    // Debug helper
    // ─────────────────────────────────────────────

    /**
     * Returns a string showing the list from MRU to LRU.
     * Example: "HEAD <-> (3,C) <-> (1,A) <-> TAIL"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HEAD");
        Node cur = head.next;
        while (cur != tail) {
            sb.append(" <-> (").append(cur.key).append(",").append(cur.value).append(")");
            cur = cur.next;
        }
        sb.append(" <-> TAIL");
        return sb.toString();
    }

    // ─────────────────────────────────────────────
    // Main — demonstration
    // ─────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== LRU Cache Demo (capacity = 3) ===\n");

        LRUCache cache = new LRUCache(3);

        // ── put operations ──
        cache.put(1, 10);
        System.out.println("put(1,10) → " + cache);

        cache.put(2, 20);
        System.out.println("put(2,20) → " + cache);

        cache.put(3, 30);
        System.out.println("put(3,30) → " + cache);

        // ── get (should move key 1 to front) ──
        System.out.println("\nget(1)    → " + cache.get(1) + "  (expected: 10)");
        System.out.println("           " + cache);

        // ── put when full (key 2 is now LRU, should be evicted) ──
        cache.put(4, 40);
        System.out.println("\nput(4,40) → " + cache + "  [key 2 evicted]");

        // ── get evicted key ──
        System.out.println("\nget(2)    → " + cache.get(2) + "  (expected: -1, was evicted)");

        // ── get remaining keys ──
        System.out.println("get(3)    → " + cache.get(3) + "  (expected: 30)");
        System.out.println("get(1)    → " + cache.get(1) + "  (expected: 10)");
        System.out.println("get(4)    → " + cache.get(4) + "  (expected: 40)");

        // ── update existing key ──
        cache.put(3, 99);
        System.out.println("\nput(3,99) → " + cache + "  [key 3 updated]");
        System.out.println("get(3)    → " + cache.get(3) + "  (expected: 99)");

        System.out.println("\n=== LeetCode-style test ===\n");

        // LeetCode 146 example
        LRUCache lc = new LRUCache(2);
        lc.put(1, 1);   // cache: {1=1}
        lc.put(2, 2);   // cache: {1=1, 2=2}
        System.out.println("get(1) = " + lc.get(1));   // 1
        lc.put(3, 3);   // evicts key 2 → cache: {1=1, 3=3}
        System.out.println("get(2) = " + lc.get(2));   // -1
        lc.put(4, 4);   // evicts key 1 → cache: {4=4, 3=3}
        System.out.println("get(1) = " + lc.get(1));   // -1
        System.out.println("get(3) = " + lc.get(3));   // 3
        System.out.println("get(4) = " + lc.get(4));   // 4
        System.out.println("\nAll expected: 1, -1, -1, 3, 4");
    }
}