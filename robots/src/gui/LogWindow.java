package gui;

import java.awt.*;
import javax.swing.JPanel;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import javax.swing.JTextArea;
import java.awt.BorderLayout;


public class LogWindow extends MyWindow implements LogChangeListener {
    private final LogWindowSource m_logSource;
    private final JTextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", "logWindow");
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new JTextArea();
        m_logContent.setEditable(false);

        minHeight = 500;
        minWidth = 200;
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        setLocation(10, 10);
        setSize(200, 500);
        setMinimumSize(getSize());

        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}