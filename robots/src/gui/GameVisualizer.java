package gui;

import gui.models.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class GameVisualizer extends JPanel {
    private final Robot robot;

    public GameVisualizer(Robot robot) {
        this.robot = robot;
        setDoubleBuffered(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, (int) robot.getX(), (int) robot.getY(), robot.getDirection());
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);

        g.setColor(Color.MAGENTA);
        g.fillOval(x - 15, y - 5, 30, 10);
        g.setColor(Color.BLACK);
        g.drawOval(x - 15, y - 5, 30, 10);

        g.setColor(Color.WHITE);
        g.fillOval(x - 15 + 25, y - 2, 5, 5);
        g.setColor(Color.BLACK);
        g.drawOval(x - 15 + 25, y - 2, 5, 5);
    }
}
