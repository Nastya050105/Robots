package robots.log;

import java.lang.ref.WeakReference;
import java.util.*;

public class LogWindowSource {
    private final int m_iQueueLength;
    private final Deque<LogEntry> m_messages;
    private final List<WeakReference<LogChangeListener>> m_listeners = new ArrayList<>();
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        this.m_iQueueLength = iQueueLength;
        this.m_messages = new ArrayDeque<>(iQueueLength);
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.add(new WeakReference<>(listener));
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.removeIf(ref -> {
                LogChangeListener l = ref.get();
                return l == null || l == listener;
            });
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized (m_messages) {
            if (m_messages.size() >= m_iQueueLength) {
                m_messages.pollFirst();
            }
            m_messages.addLast(entry);
        }

        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                List<LogChangeListener> snapshot = new ArrayList<>();
                for (WeakReference<LogChangeListener> ref : m_listeners) {
                    LogChangeListener l = ref.get();
                    if (l != null) {
                        snapshot.add(l);
                    }
                }
                activeListeners = snapshot.toArray(new LogChangeListener[0]);
                m_activeListeners = activeListeners;
            }
        }

        for (LogChangeListener listener : activeListeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        synchronized (m_messages) {
            if (startFrom < 0 || startFrom >= m_messages.size()) {
                return Collections.emptyList();
            }
            List<LogEntry> list = new ArrayList<>(m_messages);
            int indexTo = Math.min(startFrom + count, list.size());
            return list.subList(startFrom, indexTo);
        }
    }

    public Iterable<LogEntry> all() {
        synchronized (m_messages) {
            return new ArrayList<>(m_messages);
        }
    }
}
