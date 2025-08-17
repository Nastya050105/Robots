package robots.gui;

import java.io.File;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

import javax.swing.*;

import robots.log.Logger;


public class MainApplicationFrame extends JFrame
{
    private final LogWindow logWindow;
    private final GameWindow gameWindow;
    private final MainController controller;
    private final RobotPositionWindow robotPositionWindow;


    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame(LogWindow logWindow, GameWindow gameWindow, MainController mainController, RobotPositionWindow robotPositionWindow) {
        this.logWindow = logWindow;
        this.gameWindow = gameWindow;
        this.controller = mainController;
        this.robotPositionWindow = robotPositionWindow;

        initialize();
    }

    private void initialize() {
        gameWindow.setName("gameWindow");
        logWindow.setName("logWindow");


        setLogWindow(logWindow);
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);
        gameWindow.setSize(400,  400);

        addWindow(logWindow);
        // addWindow(gameWindow);
        addWindow(robotPositionWindow);

        addWindow(new NewSnakeWindow());

        add(new SnakePanel());

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        restoreWindowStates();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }


    protected void setLogWindow(LogWindow logWindow )
    {
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exitItem = new JMenuItem("Выход");

        exitItem.addActionListener(_ -> handleExit());
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

        JMenuItem systemItem = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemItem.addActionListener(_ -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            invalidate();
        });

        JMenuItem crossItem = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossItem.addActionListener(_ -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            invalidate();
        });

        lookAndFeelMenu.add(systemItem);
        lookAndFeelMenu.add(crossItem);

        return lookAndFeelMenu;
    }


    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener(_ -> controller.onAddLogMessage());
            testMenu.add(addLogMessageItem);
        }

        return testMenu;
    }


    private void handleExit() {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            controller.handleExit();
        }
    }


    private void setLookAndFeel(String className) {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }


    public void saveWindowStates() {
        Properties props = new Properties();

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String name = frame.getName();
            Rectangle bounds = frame.getBounds();

            props.setProperty(name + ".x", String.valueOf(bounds.x));
            props.setProperty(name + ".y", String.valueOf(bounds.y));
            props.setProperty(name + ".width", String.valueOf(bounds.width));
            props.setProperty(name + ".height", String.valueOf(bounds.height));
            try {
                props.setProperty(name + ".isIcon", String.valueOf(frame.isIcon()));
            } catch (Exception e) {
                Logger.error("Ошибка при получении isIcon: " + e.getMessage());
            }
            props.setProperty(name + ".isMaximized", String.valueOf(frame.isMaximum()));
        }

        try (FileOutputStream out = new FileOutputStream(WindowConfig.getConfigFile())) {
            props.store(out, "Window state");
        } catch (IOException e) {
            Logger.error("Ошибка при восстановлении положения окна " + e.getMessage());
        }
    }


    public void restoreWindowStates() {
        File file = WindowConfig.getConfigFile();
        if (!file.exists()) return;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            Logger.error("Ошибка при установке LookAndFeel: " + e.getMessage());
        }

        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String name = frame.getName();
            try {
                int x = parseIntSafe(props, name + ".x", 100);
                int y = parseIntSafe(props, name + ".y", 100);
                int width = parseIntSafe(props, name + ".width", 300);
                int height = parseIntSafe(props, name + ".height", 300);

                frame.setBounds(x, y, width, height);

                if (Boolean.parseBoolean(props.getProperty(name + ".isIcon", "false")))
                    frame.setIcon(true);
                if (Boolean.parseBoolean(props.getProperty(name + ".isMaximized", "false")))
                    frame.setMaximum(true);

            } catch (Exception e) {
                Logger.error("Ошибка при восстановлении положения окна " + name + ": " + e.getMessage());
            }
        }
    }


    private int parseIntSafe(Properties props, String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

}
