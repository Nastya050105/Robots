package log;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentRingBufferTests {

    @Test
    void testAddAndSize() {
        ConcurrentRingBuffer<Integer> buffer = new ConcurrentRingBuffer<>(3);

        assertEquals(0, buffer.size());

        buffer.add(1);
        buffer.add(2);
        assertEquals(2, buffer.size());

        buffer.add(3);
        buffer.add(4);
        assertEquals(3, buffer.size());
    }

    @Test
    void testGetBySequence() {
        ConcurrentRingBuffer<String> buffer = new ConcurrentRingBuffer<>(2);
        buffer.add("a");
        buffer.add("b");

        assertEquals("a", buffer.getBySequence(buffer.getSequence() - 2));
        assertEquals("b", buffer.getBySequence(buffer.getSequence() - 1));

        buffer.add("c");

        assertThrows(IndexOutOfBoundsException.class,
                () -> buffer.getBySequence(buffer.getSequence() - 3));
        assertEquals("b", buffer.getBySequence(buffer.getSequence() - 2));
        assertEquals("c", buffer.getBySequence(buffer.getSequence() - 1));
    }

    @Test
    void testGetRange() {
        ConcurrentRingBuffer<Integer> buffer = new ConcurrentRingBuffer<>(5);
        for (int i = 0; i < 5; i++) buffer.add(i);

        long firstSeq = buffer.getSequence() - buffer.size();
        long lastSeq = buffer.getSequence() - 1;

        List<Integer> range = buffer.getRange(firstSeq, lastSeq);
        assertEquals(List.of(0, 1, 2, 3, 4), range);

        List<Integer> partial = buffer.getRange(firstSeq - 10, firstSeq + 1);
        assertEquals(List.of(0, 1), partial);
    }

    @Test
    void testConcurrentAccess() throws InterruptedException, ExecutionException {
        int capacity = 100;
        ConcurrentRingBuffer<Integer> buffer = new ConcurrentRingBuffer<>(capacity);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Callable<Void> writer = () -> {
            for (int i = 0; i < 1000; i++) buffer.add(i);
            return null;
        };

        Callable<Boolean> reader = () -> {
            for (int i = 0; i < 1000; i++) {
                Iterable<Integer> snap = buffer.snapshot();
                snap.iterator().hasNext();
            }
            return true;
        };

        Future<Void> w1 = executor.submit(writer);
        Future<Void> w2 = executor.submit(writer);
        Future<Boolean> r1 = executor.submit(reader);
        Future<Boolean> r2 = executor.submit(reader);

        w1.get();
        w2.get();
        assertTrue(r1.get());
        assertTrue(r2.get());

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        assertTrue(buffer.size() <= capacity);
    }
}
