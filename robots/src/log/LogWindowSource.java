package robots.src.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogWindowSource {
    private final int m_iQueueLength;
    private final ConcurrentLinkedQueue<log.LogEntry> m_messages;
    private final ArrayList<log.LogChangeListener> m_listeners;
    private volatile log.LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        if (iQueueLength <= 0) {
            throw new IllegalArgumentException("Queue length must be positive");
        }
        this.m_iQueueLength = iQueueLength;
        this.m_messages = new ConcurrentLinkedQueue<>();
        this.m_listeners = new ArrayList<>();
    }

    public void registerListener(log.LogChangeListener listener) {
        synchronized(m_listeners) {
            if (!m_listeners.contains(listener)) {
                m_listeners.add(listener);
                m_activeListeners = null;
            }
        }
    }

    public void unregisterListener(log.LogChangeListener listener) {
        synchronized(m_listeners) {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(log.LogLevel logLevel, String strMessage) {
        log.LogEntry entry = new log.LogEntry(logLevel, strMessage);

        // Ограничение размера очереди
        synchronized(m_messages) {
            m_messages.add(entry);
            while (m_messages.size() > m_iQueueLength) {
                m_messages.poll();
            }
        }

        // Уведомление слушателей
        log.LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {
                    activeListeners = m_listeners.toArray(new log.LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }

        for (log.LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.size();
    }

    public Iterable<log.LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= m_messages.size()) {
            return Collections.emptyList();
        }

        ArrayList<log.LogEntry> messages = new ArrayList<>(m_messages);
        int indexTo = Math.min(startFrom + count, messages.size());
        return messages.subList(startFrom, indexTo);
    }

    public Iterable<log.LogEntry> all() {
        return new ArrayList<>(m_messages);
    }
}