package robots.src.gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final RobotModel robotModel = new RobotModel();
    private final MenuManager menuManager;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        menuManager = new MenuManager(this);
        setJMenuBar(menuManager.createMenuBar());

        // Создаем окна
        LogWindow logWindow = createLogWindow();
        GameWindow gameWindow = createGameWindow();
        RobotCoordinatesWindow coordsWindow = createCoordinatesWindow();

        // Добавляем окна
        addWindow(logWindow);
        addWindow(gameWindow);
        addWindow(coordsWindow);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        // Восстанавливаем состояние окон
        WindowStateManager.restoreState(new Savable[]{
                logWindow,
                gameWindow,
                coordsWindow
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow();
        logWindow.setSize(300, 800);
        logWindow.setLocation(10, 10);
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow(robotModel);
        gameWindow.setSize(400, 400);
        return gameWindow;
    }

    protected RobotCoordinatesWindow createCoordinatesWindow() {
        RobotCoordinatesWindow coordinatesWindow = new RobotCoordinatesWindow(robotModel);
        coordinatesWindow.setSize(200, 100);
        return coordinatesWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void confirmExit() {
        String[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog(this,
                "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (result == 0) {
            saveWindowStates();
            System.exit(0);
        }
    }

    private void saveWindowStates() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        Savable[] savableFrames = Arrays.stream(frames)
                .filter(f -> f instanceof Savable)
                .toArray(Savable[]::new);
        WindowStateManager.saveState(savableFrames);
    }
}