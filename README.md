# LRU Cache Implementation in Java

A Least Recently Used (LRU) Cache implemented from scratch in Java using HashMap and Doubly Linked List, achieving O(1) time complexity for both get and put operations.

## What is LRU Cache?

An LRU Cache evicts the least recently used item when the cache reaches its capacity. It is widely used in operating systems, databases, and browsers to optimize memory usage.

## Data Structures Used

- **HashMap** – for O(1) key lookup
- **Doubly Linked List** – for O(1) insertion and deletion to track usage order

## Time Complexity

| Operation | Complexity |
|-----------|------------|
| get(key) | O(1) |
| put(key, value) | O(1) |

## How It Works

1. Every time a key is accessed (get) or inserted (put), it moves to the front of the list (most recently used).
2. When capacity is full, the node at the tail (least recently used) is removed.
3. HashMap stores key → node reference for instant access.

## Project Structure

```
LRUCache/
├── LRUCache.java       # Core cache implementation
├── Node.java           # Doubly linked list node
└── Main.java           # Test cases and driver code
```

## Usage

```java
LRUCache cache = new LRUCache(3);
cache.put(1, 10);
cache.put(2, 20);
cache.put(3, 30);
cache.get(1);       // returns 10, moves 1 to front
cache.put(4, 40);   // evicts key 2 (least recently used)
cache.get(2);       // returns -1 (evicted)
```

## Test Cases Covered

- Basic get and put operations
- Eviction when capacity is reached
- Accessing existing keys updates usage order
- Boundary capacity (capacity = 1)
- Concurrent access scenarios

## Key Learnings

- Efficient cache design using combined data structures
- Memory optimization and eviction strategies
- O(1) operations through pointer manipulation
