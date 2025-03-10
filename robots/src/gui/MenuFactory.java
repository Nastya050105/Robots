package gui;

import javax.swing.*;

public class MenuFactory {
    public static JMenuBar createMenuBar(MainApplicationFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new LookAndFeelMenu(frame));
        menuBar.add(new TestMenu());
        menuBar.add(new ExitMenu(frame));
        return menuBar;
    }
}
