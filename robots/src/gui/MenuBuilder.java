package src.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;


import src.log.Logger;

public class MenuBuilder {

    private final JMenuBar menuBar = new JMenuBar();
    private final JFrame mainFrame;

    public MenuBuilder(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }


    public JMenuBar buildMenuBar() {
        addFileMenu();
        addLookAndFeelMenu();
        addTestMenu();
        return menuBar;
    }

    private void addFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("Файл");

        addMenuItem(fileMenu, "Выход", KeyEvent.VK_X, () -> {
            int confirmed = JOptionPane.showOptionDialog(
                    null, "Вы уверены?", "подтверждение выхода",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{"Да", "Нет"}, "Нет");
            if (confirmed == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        menuBar.add(fileMenu);
    }

    private void addLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        addMenuItem(lookAndFeelMenu, "Системная схема", KeyEvent.VK_S, () -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        });

        addMenuItem(lookAndFeelMenu, "Универсальная схема", KeyEvent.VK_U, () -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        });

        menuBar.add(lookAndFeelMenu);
    }

    private void addTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        addMenuItem(testMenu, "Сообщение в лог", KeyEvent.VK_S, () -> {
            Logger.debug("Новая строка");
        });

        menuBar.add(testMenu);
    }

    private void addMenuItem(JMenu menu, String text, int mnemonic, Runnable action) {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.addActionListener(e -> action.run());
        menu.add(menuItem);
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI((JFrame) menuBar.getTopLevelAncestor());
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }

    public void confirmExit() {
        int confirmed = JOptionPane.showOptionDialog(
                mainFrame,
                "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Да", "Нет"},
                "Нет"
        );
        if (confirmed == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}