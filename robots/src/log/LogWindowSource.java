package src.log;


import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений
 * ограниченного размера)
 */
public class LogWindowSource {
    private final int m_iQueueLength;
    private final LinkedList<LogEntry> m_messages;
    private final List<WeakReference<LogChangeListener>> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.add(new WeakReference<>(listener));
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized (m_listeners) {
            m_listeners.removeIf(ref -> {
                LogChangeListener l = ref.get();
                return l == null || l == listener;
            });
        }
    }

    public void clearListeners() {
        synchronized (m_listeners) {
            m_listeners.clear();
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized (m_messages) {
            m_messages.addLast(entry);
            if (m_messages.size() > m_iQueueLength) {
                m_messages.removeFirst();
            }
        }


        List<LogChangeListener> activeListeners = new ArrayList<>();
        synchronized (m_listeners) {
            Iterator<WeakReference<LogChangeListener>> iterator = m_listeners.iterator();
            while (iterator.hasNext()) {
                LogChangeListener listener = iterator.next().get();
                if (listener != null) {
                    activeListeners.add(listener);
                } else {
                    iterator.remove(); // Удаляем мёртвые ссылки
                }
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
            int indexTo = Math.min(startFrom + count, m_messages.size());
            return new ArrayList<>(m_messages.subList(startFrom, indexTo));
        }
    }

    public Iterable<LogEntry> all() {
        return m_messages;
    }
}