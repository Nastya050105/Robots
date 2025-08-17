package robots.gui;

public class AppContext {
    public final LogWindow logWindow;
    public final GameWindow gameWindow;
    public final RobotPositionWindow positionWindow;

    public AppContext(LogWindow logWindow, GameWindow gameWindow, RobotPositionWindow positionWindow) {
        this.logWindow = logWindow;
        this.gameWindow = gameWindow;
        this.positionWindow = positionWindow;
    }
}
