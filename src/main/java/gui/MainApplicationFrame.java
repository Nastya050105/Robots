package gui;

import gui.models.Robot;
import gui.persistence.WindowsStateManager;
import gui.utils.Localizable;
import gui.utils.Localization;
import gui.utils.OnExitPopup;
import gui.windows.GameWindow;
import gui.windows.LogWindow;
import gui.windows.RobotInfoWindow;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MainApplicationFrame extends JFrame implements Localizable {
    private final JDesktopPane desktopPane = new JDesktopPane();

    private final WindowsStateManager windowsStateManager = new WindowsStateManager(".");
    private final List<Localizable> localizables = new ArrayList<>();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        Robot robot = new Robot();

        RobotInfoWindow robotInfoWindow = new RobotInfoWindow(robot);
        robotInfoWindow.setSize(400, 400);
        addWindow(robotInfoWindow);

        GameWindow gameWindow = new GameWindow(robot);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        windowsStateManager.load(desktopPane);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                OnExitPopup.show(MainApplicationFrame.this, () -> windowsStateManager.save(desktopPane));
            }
        });

        localizables.add(this);
        localizables.add(logWindow);
        localizables.add(robotInfoWindow);
        localizables.add(gameWindow);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(Localization.getValue("log.started"));
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu testMenu = new JMenu(Localization.getValue("menu.tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(Localization.getValue("menu.tests.description"));
        testMenu.add(createMenuItem(
                Localization.getValue("menu.tests.log"),
                (event) -> Logger.debug(Localization.getValue("log.newline"))
        ));

        JButton exitButton = new JButton(Localization.getValue("menu.exit"));
        exitButton.addActionListener(e ->
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING))
        );

        menuBar.add(createViewMenu());
        menuBar.add(testMenu);
        menuBar.add(createLanguageMenu());
        menuBar.add(exitButton);

        return menuBar;
    }

    private JMenu createViewMenu() {
        JMenu lookAndFeelMenu = new JMenu(Localization.getValue("menu.view"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(Localization.getValue("menu.view.description"));

        lookAndFeelMenu.add(createMenuItem(Localization.getValue("menu.view.system"), (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        }));
        lookAndFeelMenu.add(createMenuItem(Localization.getValue("menu.view.cross"), (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        }));

        return lookAndFeelMenu;
    }

    private JMenu createLanguageMenu() {
        Consumer<Locale> switchLanguage = (locale) -> {
            Localization.setLocale(locale);

            for (Localizable l : localizables) {
                l.updateLocalization();
            }

            SwingUtilities.updateComponentTreeUI(this);
            this.invalidate();
            this.repaint();
        };

        JMenu langMenu = new JMenu(Localization.getValue("menu.language"));

        langMenu.add(createMenuItem("Русский", (event) -> switchLanguage.accept(Locale.of("ru", "RU"))));
        langMenu.add(createMenuItem("English", (event) -> switchLanguage.accept(Locale.ENGLISH)));

        return langMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    private static JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text, KeyEvent.VK_S);
        item.addActionListener(listener);

        return item;
    }

    @Override
    public void updateLocalization() {
        setJMenuBar(generateMenuBar());
    }
}
