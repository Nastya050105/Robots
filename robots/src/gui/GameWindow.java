package gui;

import view.GameVisualizer;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JInternalFrame {
    public GameWindow(GameVisualizer visualizer) {
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}