package gui.windows;

import gui.GameVisualizer;
import gui.models.Robot;
import gui.utils.Localizable;
import gui.utils.Localization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class GameWindowController extends JInternalFrame implements Localizable, Observer {
    private final GameVisualizer visualizer;

    public GameWindowController(Robot robot) {
        super(Localization.getValue("game.title"), true, true, true, true);

        visualizer = new GameVisualizer(robot);

        robot.addObserver(this);
        robot.setBoundaries(visualizer.getWidth(), visualizer.getHeight());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        visualizer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                robot.setBoundaries(visualizer.getWidth(), visualizer.getHeight());
            }
        });

        visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                robot.setTarget(e.getPoint().getX(), e.getPoint().getY());
            }
        });
    }

    @Override
    public void updateLocalization() {
        setTitle(Localization.getValue("game.title"));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Robot) {
            visualizer.repaint();
        }
    }
}
