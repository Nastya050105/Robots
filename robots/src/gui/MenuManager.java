package gui;

import java.awt.event.KeyEvent;
import javax.swing.*;

import log.Logger;

public class MenuManager {

    private final MainApplicationFrame mainFrame;

    public MenuManager(MainApplicationFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

        lookAndFeelMenu.add(createLookAndFeelMenuItem("Системная схема", UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(createLookAndFeelMenuItem("Универсальная схема", UIManager.getCrossPlatformLookAndFeelClassName()));

        return lookAndFeelMenu;
    }

    private JMenuItem createLookAndFeelMenuItem(String title, String className) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener((event) -> {
            setLookAndFeel(className);
            mainFrame.invalidate();
        });
        return menuItem;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            try {
                UIManager.setLookAndFeel(className);
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            SwingUtilities.updateComponentTreeUI(mainFrame);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // just ignore
        }
    }
}
