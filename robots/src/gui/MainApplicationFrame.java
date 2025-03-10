package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.Properties;

import controller.GameController;
import log.Logger;
import model.RobotModel;
import view.GameVisualizer;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + "robotAppConfig.properties";

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow();
        addWindow(gameWindow);

        setJMenuBar(MenuFactory.createMenuBar(this));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                exitApplication();
            }
        });

        restoreWindowStates();
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow() {
        RobotModel model = new RobotModel();
        GameVisualizer visualizer = new GameVisualizer(model);
        GameWindow gameWindow = new GameWindow(visualizer);
        GameController controller = new GameController(model, visualizer);
        gameWindow.setSize(400, 400);
        return gameWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void exitApplication() {
        Object[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (result == 0) {
            saveWindowStates();
            System.exit(0);
        }
    }

    private void saveWindowStates() {
        Properties properties = new Properties();
        for (Component comp : desktopPane.getComponents()) {
            if (comp instanceof JInternalFrame) {
                JInternalFrame frame = (JInternalFrame) comp;
                properties.setProperty(frame.getTitle() + ".x", String.valueOf(frame.getX()));
                properties.setProperty(frame.getTitle() + ".y", String.valueOf(frame.getY()));
                properties.setProperty(frame.getTitle() + ".width", String.valueOf(frame.getWidth()));
                properties.setProperty(frame.getTitle() + ".height", String.valueOf(frame.getHeight()));
                properties.setProperty(frame.getTitle() + ".maximized", String.valueOf(frame.isMaximum()));
            }
        }
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreWindowStates() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            for (Component comp : desktopPane.getComponents()) {
                if (comp instanceof JInternalFrame) {
                    JInternalFrame frame = (JInternalFrame) comp;
                    String title = frame.getTitle();
                    int x = Integer.parseInt(properties.getProperty(title + ".x", String.valueOf(frame.getX())));
                    int y = Integer.parseInt(properties.getProperty(title + ".y", String.valueOf(frame.getY())));
                    int width = Integer.parseInt(properties.getProperty(title + ".width", String.valueOf(frame.getWidth())));
                    int height = Integer.parseInt(properties.getProperty(title + ".height", String.valueOf(frame.getHeight())));
                    boolean maximized = Boolean.parseBoolean(properties.getProperty(title + ".maximized", String.valueOf(frame.isMaximum())));

                    frame.setBounds(x, y, width, height);
                    try {
                        frame.setMaximum(maximized);
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {

        }
    }
}
