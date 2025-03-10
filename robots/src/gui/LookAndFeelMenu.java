package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class LookAndFeelMenu extends JMenu {
    public LookAndFeelMenu(MainApplicationFrame frame) {
        super("Режим отображения");
        setMnemonic(KeyEvent.VK_V);
        getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

        add(createMenuItem("Системная схема", UIManager.getSystemLookAndFeelClassName(), frame));
        add(createMenuItem("Универсальная схема", UIManager.getCrossPlatformLookAndFeelClassName(), frame));
    }

    private JMenuItem createMenuItem(String label, String className, MainApplicationFrame frame) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener((_) -> {
            frame.setLookAndFeel(className);
            frame.invalidate();
        });
        return item;
    }
}
