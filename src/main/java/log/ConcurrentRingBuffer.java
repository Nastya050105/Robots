package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Потокобезопасный кольцевой буфер фиксированного размера, предназначенный для эффективного
 * хранения записей логов или других потоков данных, упорядоченных по времени, где старые элементы
 * автоматически удаляются при переполнении буфера.
 *
 * <h2>Назначение</h2>
 * Этот буфер используется в сценариях, где:
 * <ul>
 *     <li>Необходимо ограниченное хранение данных с автоматическим вытеснением старых элементов.</li>
 *     <li>Вставка новых элементов должна быть быстрой и не требовать сдвига существующих (O(1)).</li>
 *     <li>Чтение элементов должно быть возможным по логическому последовательному индексу (sequence number),
 *         который независим от физического индекса в массиве.</li>
 *     <li>Потокобезопасность при одновременном чтении и записи.</li>
 *     <li>Итератор должен быть стабильным, даже если новые элементы добавляются в буфер (fail-safe).</li>
 * </ul>
 *
 * <h2>Решаемая проблема</h2>
 * Обычные структуры данных, такие как {@link java.util.ArrayList} или
 * {@link java.util.concurrent.BlockingQueue}, не подходят для реализации окна логов, потому что:
 * <ul>
 *     <li>Удаление старых элементов из начала ArrayList занимает O(n) из-за сдвига элементов.</li>
 *     <li>Многие конкурентные коллекции предоставляют fail-fast итераторы, которые ломаются при изменении коллекции.</li>
 *     <li>Нет эффективного способа доступа к диапазону элементов по логическому индексу.</li>
 * </ul>
 * Кольцевой буфер решает эти проблемы, используя фиксированный массив, где новые элементы
 * перезаписывают самые старые при переполнении. Логические индексы (sequence numbers) позволяют
 * безопасно и эффективно получать доступ к элементам.
 *
 * <h2>Идеи реализации</h2>
 * <ul>
 *     <li>Фиксированный массив для хранения элементов.</li>
 *     <li>Два указателя:
 *         <ul>
 *             <li>{@code head} — индекс старейшего элемента в массиве.</li>
 *             <li>{@code size} — количество элементов в буфере.</li>
 *         </ul>
 *     </li>
 *     <li>Глобальный логический индекс {@code nextSequence} для стабильного обращения к элементам.</li>
 *     <li>Потокобезопасность обеспечивается с помощью {@link java.util.concurrent.locks.ReentrantReadWriteLock}:
 *         <ul>
 *             <li>Запись — через write-lock.</li>
 *             <li>Чтение — через read-lock.</li>
 *             <li>Создание snapshot для итерации копирует ссылки на элементы, не блокируя запись надолго.</li>
 *         </ul>
 *     </li>
 *     <li>Вставка и доступ к элементам выполняются за O(1).</li>
 * </ul>
 *
 * <h2>Типовые сценарии использования</h2>
 * <ul>
 *     <li>Окна логов в IDE и отладчиках.</li>
 *     <li>Панели мониторинга в реальном времени.</li>
 *     <li>Телеметрия и rolling-логи.</li>
 *     <li>Системы, где старые данные не должны накапливаться бесконечно.</li>
 * </ul>
 *
 * <h2>Потокобезопасность</h2>
 * Все операции буфера потокобезопасны:
 * <ul>
 *     <li>Множество потоков могут одновременно читать данные.</li>
 *     <li>Один поток может писать за раз.</li>
 *     <li>Итераторы, полученные через {@link #snapshot()}, стабильны и не ломаются при добавлении новых элементов.</li>
 * </ul>
 *
 * <h2>Ограничения</h2>
 * <ul>
 *     <li>Старые данные безвозвратно теряются при перезаписи.</li>
 *     <li>Логические индексы {@code nextSequence} растут без ограничения (переполнение произойдет
 *         только после миллиардов вставок).</li>
 * </ul>
 *
 * @param <T> тип элементов, хранящихся в буфере
 */
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
                // переполнение: вытесняем старый элемент
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
