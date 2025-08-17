package robots.gui;

import java.awt.*;

import robots.log.Logger;
import robots.log.RobotPositionSource;

public class MainController {
    private final LogWindow logWindow;
    private final GameWindow gameWindow;
    private MainApplicationFrame frame;
    private final RobotPositionWindow robotPositionWindow;
    private final SnakePanel snakePanel;

    public MainController() {
        this.snakePanel = new SnakePanel();
        this.logWindow = new LogWindow(Logger.getDefaultLogSource());
        this.gameWindow = new GameWindow();
        this.robotPositionWindow = new RobotPositionWindow(Logger.getrobotPositionModel());
    }

    public void createFrame() {
        frame = new MainApplicationFrame(logWindow, gameWindow, this, robotPositionWindow);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    public void onAddLogMessage() {
        Logger.debug("Новая строка");
    }

    public void handleExit() {
        if (frame != null)
            frame.saveWindowStates();
        System.exit(0);
    }

}
