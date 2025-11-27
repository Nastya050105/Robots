package gui.windows;

import gui.models.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class RobotInfoWindow extends JInternalFrame implements Observer {
    private final JLabel coordsLabel = new JLabel();

    public RobotInfoWindow(Robot robot) {
        super("Позиция робота", true, true, true, true);

        robot.addObserver(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordsLabel, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Robot robot) {
            coordsLabel.setText(String.format("X: %.2f, Y: %.2f", robot.getX(), robot.getY()));
        }
    }
}
