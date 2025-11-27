package log;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogWindowSource {
    private final BlockingQueue<LogEntry> m_messages;
    private final List<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_messages = new ArrayBlockingQueue<>(iQueueLength);
        m_listeners = new CopyOnWriteArrayList<>();
    }

    public void registerListener(LogChangeListener listener) {
        m_listeners.add(listener);
    }

    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    public int getCountListener() {
        return m_listeners.size();
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        boolean shrunk;
        do {
            shrunk = m_messages.offer(entry);
            if (!shrunk) {
                m_messages.poll();
            }
        } while (!shrunk);

        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.size();
    }

    public Iterable<LogEntry> all() {
        return m_messages;
    }
}
