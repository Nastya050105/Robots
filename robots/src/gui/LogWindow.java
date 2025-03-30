package src.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import src.log.LogChangeListener;
import src.log.LogEntry;
import src.log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener {
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this); // Подписка на изменения лога

        m_logContent = new TextArea();
        m_logContent.setEditable(false);
        m_logContent.setPreferredSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                dispose();   // закрываем окно корректно, чтобы слушатели не копились
            }
        });

        updateLogContent();
        pack();
        setVisible(true);
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        synchronized (m_logSource) {
            for (LogEntry entry : m_logSource.all()) {
                content.append(entry.getMessage()).append("\n");
            }
        }
        m_logContent.setText(content.toString());
        m_logContent.repaint();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void dispose() {
        // Удаляем LogWindow когда закрываем окно это чтобы память не утекала в трубу и метод
        // переопределили потому что такой уже есть в JInternalFrame
        m_logSource.unregisterListener(this);
        super.dispose();
    }
}
