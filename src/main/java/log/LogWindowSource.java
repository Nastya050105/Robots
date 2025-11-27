package log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogWindowSource {
    private final ConcurrentRingBuffer<LogEntry> buffer;
    private final List<LogChangeListener> listeners = new CopyOnWriteArrayList<>();

    public LogWindowSource(int limit) {
        buffer = new ConcurrentRingBuffer<>(limit);
    }

    public void registerListener(LogChangeListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(LogChangeListener listener) {
        listeners.remove(listener);
    }

    public int getCountListener() {
        return listeners.size();
    }

    public void append(LogLevel logLevel, String strMessage) {
        buffer.add(new LogEntry(logLevel, strMessage));
        for (var l : listeners) {
            l.onLogChanged();
        }
    }

    public int size() {
        return buffer.size();
    }

    public Iterable<LogEntry> all() {
        return buffer.snapshot();
    }
}
