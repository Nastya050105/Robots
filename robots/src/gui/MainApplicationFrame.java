package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final MenuManager menuManager;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        gui.LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        gui.GameWindow gameWindow = new gui.GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        menuManager = new MenuManager(this);
        setJMenuBar(menuManager.createMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected gui.LogWindow createLogWindow() {
        gui.LogWindow logWindow = new gui.LogWindow();
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
}