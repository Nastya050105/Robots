package robots.src.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import log.LogChangeListener;
import log.LogEntry;
import robots.src.log.LogWindowSource;

import java.beans.PropertyVetoException;
import java.util.Map;

public class LogWindow extends JInternalFrame implements LogChangeListener, Savable {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;
    private LogEntry entry;


    public LogWindow(LogWindowSource logSource)
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    public LogWindow() {

    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (m_logSource.all(); ;) content.append(entry.getMessage()).append("\n");
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void saveState(Map<String, String> state) {
        PrefixedStateMap logState = new PrefixedStateMap(state, "log.");
        state.put("log.width", String.valueOf(getWidth()));
        state.put("log.height", String.valueOf(getHeight()));
        state.put("log.x", String.valueOf(getX()));
        state.put("log.y", String.valueOf(getY()));
        state.put("log.isMaximized", String.valueOf(isMaximum()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        PrefixedStateMap logState = new PrefixedStateMap(state, "log.");
        if (!logState.isEmpty()) {
            setBounds(
                    Integer.parseInt(logState.getOrDefault("x", "10")),
                    Integer.parseInt(logState.getOrDefault("y", "10")),
                    Integer.parseInt(logState.getOrDefault("width", "300")),
                    Integer.parseInt(logState.getOrDefault("height", "800"))
            );
            try {
                setMaximum(Boolean.parseBoolean(logState.get("isMaximized")));
            } catch (Exception ignored) {
            }
        }
    }
}
