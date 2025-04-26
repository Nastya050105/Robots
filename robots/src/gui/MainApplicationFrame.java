package robots.src.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import log.Logger;

import java.util.HashMap;
import java.util.Map;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final MenuManager menuManager;

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        gui.GameWindow gameWindow = new gui.GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        menuManager = new MenuManager(this);
        setJMenuBar(menuManager.createMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //чтобы окно не закрылось автоматически
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        //слушатель для обработки события закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
        // Восстановление состояния окон при запуске
        WindowStateManager.restoreState(new Savable[]{logWindow, gameWindow});
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow();
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

    //подтверждение выхода
    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите выйти?", "Подтверждение выхода", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            // Сохранение состояния окон перед выходом
            try {
                if (desktopPane.getComponentCount() >= 2) {
                    WindowStateManager.saveState(new Savable[]{
                            (Savable) desktopPane.getComponent(0), // LogWindow
                            (Savable) desktopPane.getComponent(1)  // GameWindow
                    });
                }
                // Закрываем окно
                System.exit(0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
