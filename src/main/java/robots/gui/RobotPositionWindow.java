package robots.gui;

import robots.log.RobotPositionListener;
import robots.log.RobotPositionSource;

import javax.swing.*;

public class RobotPositionWindow extends JInternalFrame implements RobotPositionListener {
    private final JLabel label;

    public RobotPositionWindow(RobotPositionSource source) {
        super("Координаты робота", true, true, true, true);
        label = new JLabel("x: 0, y: 0");
        getContentPane().add(label);
        pack();
        source.registerListener(this);
    }

    @Override
    public void onPositionChanged(double x, double y) {
        SwingUtilities.invokeLater(() -> label.setText("x: " + (int)x + ", y: " + (int)y));
    }
}
