package robots.src.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
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
        menuBar.add(createExitMenuItem()); // Добавление пункта меню "Выход"
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

    private JMenuItem createExitMenuItem() {
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitItem.addActionListener((event) -> {
            // Генерация события закрытия окна
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        });
        return exitItem;
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