package windows;

import log.LogChangeListener;
import log.LogLevel;
import log.LogWindowSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogWindowSourceTests {
    private LogWindowSource logWindowSource;

    @BeforeEach
    public void setUp() {
        logWindowSource = new LogWindowSource(5);
    }

    @Test
    void testRegisterListener() {
        LogChangeListener listener = () -> {
        };
        logWindowSource.registerListener(listener);
        assertEquals(1, logWindowSource.getCountListener());
    }

    @Test
    void testUnregisterListener() {
        LogChangeListener listener = () -> {
        };
        logWindowSource.registerListener(listener);
        logWindowSource.unregisterListener(listener);

        assertEquals(0, logWindowSource.getCountListener());
    }

    @Test
    void testAppend() {
        LogChangeListener listener = () -> {
        };
        logWindowSource.registerListener(listener);
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        assertEquals(3, logWindowSource.size());
    }

    @Test
    void testOverflowQueue() {
        LogChangeListener listener = () -> {
        };
        logWindowSource.registerListener(listener);
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        assertEquals(5, logWindowSource.size());
    }
}