package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import log.Logger;


public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowManager windowManager;

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        windowManager = new WindowManager();

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = createGameWindow();
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    public void applyLookAndFeel(String className) {
        setLookAndFeel(className);
        this.invalidate(); // Обновляем интерфейс после изменения темы
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        windowManager.loadWindowState(logWindow);
        Logger.debug("LogWindow работает");
        return logWindow;
    }

    protected GameWindow createGameWindow() {
        GameWindow gameWindow = new GameWindow();
        windowManager.loadWindowState(gameWindow);
        Logger.debug("GameWindow работает");
        return gameWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    protected JMenuBar generateMenuBar() {
        return MenuBarBuilder.createMenuBar(this);
    }

    void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    public void confirmExit() {
        String message = "Вы действительно хотите выйти?";
        String title = "Подтверждение выхода";
        Object[] options = {"Да", "Нет"};

        int result = JOptionPane.showOptionDialog(
                this,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // Иконка
                options,
                options[1] // Выбранный по умолчанию вариант
        );
        if (result == JOptionPane.YES_OPTION) {
            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                if (frame instanceof Savable) {
                    windowManager.saveWindowState((MyWindow) frame);
                }
            }
            windowManager.saveToDisk();
            System.exit(0);
        }
    }
}