package gui.windows;

import gui.GameVisualizer;
import gui.models.Robot;
import gui.utils.Localizable;
import gui.utils.Localization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GameWindow extends JInternalFrame implements Localizable {
    private final GameVisualizer visualizer;

    public GameWindow(Robot robot) {
        super(Localization.getValue("game.title"), true, true, true, true);

        visualizer = new GameVisualizer(robot);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                robot.update(10); // обновление модели
                visualizer.repaint(); // перерисовка
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                robot.setTarget(e.getPoint().getX(), e.getPoint().getY());
            }
        });
    }

    @Override
    public void updateLocalization() {
        setTitle(Localization.getValue("game.title"));
    }
}
