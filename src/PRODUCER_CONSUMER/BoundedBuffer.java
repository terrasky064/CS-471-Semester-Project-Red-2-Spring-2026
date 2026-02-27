package PRODUCER_CONSUMER;
import java.util.*;
import java.util.concurrent.Semaphore;

public class BoundedBuffer {
    private final LinkedList<SalesRecord> buffer = new LinkedList<>();
    private final int capacity;
    public final Semaphore empty;
    public final Semaphore full = new Semaphore(0);
    public final Semaphore mutex = new Semaphore(1);
    // This class implements a thread-safe bounded buffer using semaphores. It provides methods for producers to put items into the buffer and for consumers to take items from the buffer. The buffer has a fixed capacity, and the semaphores ensure that producers wait when the buffer is full and consumers wait when the buffer is empty. The mutex semaphore ensures that only one thread can access the buffer at a time, preventing
    public BoundedBuffer(int cap) {
        capacity = cap;
        empty = new Semaphore(cap);
    }
    // The put method allows a producer to add a SalesRecord item to the buffer. It first acquires the empty semaphore to ensure there is space in the buffer, then acquires the mutex to gain exclusive access to the buffer. After adding the item, it releases the mutex and then releases the full semaphore to signal that there is an additional item in the buffer for consumers to take.
    public void put(SalesRecord item) throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        try {
            buffer.addLast(item);
        } finally {
            mutex.release();
        }
        full.release();
    }
    // The take method allows a consumer to remove a SalesRecord item from the buffer. It first acquires the full semaphore to ensure there is at least one item in the buffer, then acquires the mutex to gain exclusive access to the buffer. After removing an item, it releases the mutex and then releases the empty semaphore to signal that there is now additional space in the buffer for producers to add more items.
    public SalesRecord take() throws InterruptedException {
        full.acquire();
        mutex.acquire();
        SalesRecord item;
        try {
            item = buffer.removeFirst();
        } finally {
            mutex.release();
        }
        empty.release();
        return item;
    }
}