package robots.log;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class LogWindowSource {
    private final int m_iQueueLength;

    private final BlockingQueue<LogEntry> m_messages;
    private final ArrayList<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayBlockingQueue<LogEntry>(iQueueLength);
        m_listeners = new ArrayList<LogChangeListener>();
    }

    public void registerListener(LogChangeListener listener) {
        m_listeners.add(listener);
        updateActiveListeners();
    }

    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
        updateActiveListeners();
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        boolean result = false;
        do {
            result = m_messages.offer(entry);
            if (!result) {
                m_messages.poll();
            }
        } while (!result);

        notifyListeners();
    }

    public void notifyListeners() {
        LogChangeListener[] activeListeners = m_activeListeners;

        if (activeListeners != null) {
            for (LogChangeListener listener : activeListeners) {
                listener.onLogChanged();
            }
        }
    }


    private void updateActiveListeners() {
        m_activeListeners = m_listeners.toArray(new LogChangeListener[0]);
    }

    public int size() {
        return m_messages.size();
    }

    public int getCountListener() {
        return m_listeners.size();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        ArrayList<LogEntry> range = new ArrayList<>(m_messages);
        int end = Math.min(startFrom + count, range.size());
        return range.subList(startFrom, end);
    }

    public Iterable<LogEntry> all() {
        return new ArrayList<>(m_messages);
    }
}