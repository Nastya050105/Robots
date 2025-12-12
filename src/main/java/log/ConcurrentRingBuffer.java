package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentRingBuffer<T> {
    private final Object[] buffer;
    private final int capacity;

    private long nextSequence = 0;
    private int head = 0;
    private int size = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConcurrentRingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public void add(T element) {
        lock.writeLock().lock();
        try {
            int insertIndex = (head + size) % capacity;
            buffer[insertIndex] = element;

            if (size < capacity) {
                size++;
            } else {
                head = (head + 1) % capacity;
            }

            nextSequence++;

        } finally {
            lock.writeLock().unlock();
        }
    }

    public long getSequence() {
        lock.readLock().lock();
        try {
            return nextSequence;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    public T getBySequence(long seq) {
        lock.readLock().lock();
        try {
            long firstSeq = nextSequence - size;
            if (seq < firstSeq || seq >= nextSequence) {
                throw new IndexOutOfBoundsException();
            }
            int index = (int) ((head + (seq - firstSeq)) % capacity);
            return (T) buffer[index];
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<T> getRange(long fromSeq, long toSeq) {
        lock.readLock().lock();
        try {
            long firstSeq = nextSequence - size;
            long lastSeq = nextSequence - 1;

            fromSeq = Math.max(fromSeq, firstSeq);
            toSeq = Math.min(toSeq, lastSeq);

            if (fromSeq > toSeq) return Collections.emptyList();

            int count = (int) (toSeq - fromSeq + 1);
            List<T> result = new ArrayList<>(count);

            for (long seq = fromSeq; seq <= toSeq; seq++) {
                result.add(getBySequence(seq));
            }

            return result;

        } finally {
            lock.readLock().unlock();
        }
    }

    public Iterable<T> snapshot() {
        lock.readLock().lock();
        try {
            List<T> snap = new ArrayList<>(size);
            long firstSeq = nextSequence - size;
            for (long seq = firstSeq; seq < nextSequence; seq++) {
                snap.add(getBySequence(seq));
            }
            return snap;
        } finally {
            lock.readLock().unlock();
        }
    }
}
