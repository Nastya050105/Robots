package robots.gui;

import javax.swing.*;
import java.awt.*;

public class NewSnakeWindow extends JInternalFrame {
    public NewSnakeWindow() {
        super("Новая змейка", true, true, true, true);
        setSize(500, 500);
        setLocation(100, 100);
        setLayout(new BorderLayout());
        add(new SnakePanel(), BorderLayout.CENTER);
        pack();
    }
}
