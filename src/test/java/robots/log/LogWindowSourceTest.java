package robots.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import robots.gui.GameVisualizer;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class LogWindowSourceTest {
    private LogWindowSource logWindowSource;

    @BeforeEach
    public void setUp() {
        logWindowSource = new LogWindowSource(5);
    }

    @Test
    void testRegisterListener() {
        LogChangeListener listener = new LogChangeListener() {
            @Override
            public void onLogChanged() {
            }
        };
        logWindowSource.registerListener(listener);
        assertEquals(1, logWindowSource.getCountListener());
    }

    @Test
    void testUnregisterListener() {
        LogChangeListener listener = new LogChangeListener() {
            @Override
            public void onLogChanged() {
            }
        };
        logWindowSource.registerListener(listener);
        logWindowSource.unregisterListener(listener);

        assertEquals(0, logWindowSource.getCountListener());
    }

    @Test
    void testAppend() {
        LogChangeListener listener = new LogChangeListener() {
            @Override
            public void onLogChanged() {
            }
        };
        logWindowSource.registerListener(listener);
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        logWindowSource.append(LogLevel.Info, "message");
        assertEquals(3, logWindowSource.size());
    }

    @Test
    void testOverflowQueue() {
        LogChangeListener listener = new LogChangeListener() {
            @Override
            public void onLogChanged() {
            }
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