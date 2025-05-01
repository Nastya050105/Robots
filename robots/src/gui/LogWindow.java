package robots.src.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import log.LogChangeListener;
import log.LogEntry;
import log.Logger;
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
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void saveState(Map<String, String> state) {
        state.put("log.x", Integer.toString(getX()));
        state.put("log.y", Integer.toString(getY()));
        state.put("log.width", Integer.toString(getWidth()));
        state.put("log.height", Integer.toString(getHeight()));
        state.put("log.isMaximized", Boolean.toString(isMaximum()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        int x = Integer.parseInt(state.getOrDefault("log.x", "10"));
        int y = Integer.parseInt(state.getOrDefault("log.y", "10"));
        int width = Integer.parseInt(state.getOrDefault("log.width", "300"));
        int height = Integer.parseInt(state.getOrDefault("log.height", "800"));
        setBounds(x, y, width, height);

        try {
            setMaximum(Boolean.parseBoolean(state.getOrDefault("log.isMaximized", "false")));
        } catch (Exception e) {
            Logger.error("Ошибка восстановления состояния окна: " + e.getMessage());
        }
    }
}
