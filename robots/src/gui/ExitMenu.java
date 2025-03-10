package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class ExitMenu extends JMenu {
    public ExitMenu(MainApplicationFrame frame) {
        super("Выход");
        setMnemonic(KeyEvent.VK_X);

        JMenuItem exitMenuItem = new JMenuItem("Выйти", KeyEvent.VK_X);
        exitMenuItem.addActionListener((_) -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
        add(exitMenuItem);
    }
}
