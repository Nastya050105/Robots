package gui.windows;

import gui.GameVisualizer;
import gui.models.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GameWindow extends JInternalFrame {
    private final GameVisualizer visualizer;

    public GameWindow(Robot robot) {
        super("Игровое поле", true, true, true, true);

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
}
