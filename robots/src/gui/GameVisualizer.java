package src.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class GameVisualizer extends JPanel {

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    public GameVisualizer() {
        initializeTimer();
        addMouseListener(new MouseClickListener());
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(400, 400));
        setSize(500, 500);
    }
    public double getM_robotPositionX(){
        return m_robotPositionX;
    }

    public double getM_robotPositionY() {
        return m_robotPositionY;
    }

    private void initializeTimer() {
        Timer timer = new Timer("events generator", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
    }

    private class MouseClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            setTargetPosition(e.getPoint());
            repaint();
        }
    }

    public void setTargetPosition(Point point) {
        System.out.println("Click at: (" + point.x + ", " + point.y + ")");
        m_targetPositionX = point.x;
        m_targetPositionY = point.y;
        System.out.println("Target set at: (" + m_targetPositionX + ", " + m_targetPositionY + ")");
        repaint();
    }


    private void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    public void onModelUpdateEvent() {
        double distanceToTarget = calculateDistance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distanceToTarget < 0.5) {
            return;
        }

        double angleToTarget = calculateAngleToTarget(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = calculateAngularVelocity(angleToTarget, m_robotDirection);

        moveRobot(MAX_VELOCITY, angularVelocity, 10);
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private double calculateAngleToTarget(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return normalizeAngle(Math.atan2(diffY, diffX));
    }

    private double calculateAngularVelocity(double angleToTarget, double currentDirection) {
        if (angleToTarget > currentDirection) {
            return MAX_ANGULAR_VELOCITY;
        } else if (angleToTarget < currentDirection) {
            return -MAX_ANGULAR_VELOCITY;
        }
        return 0;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }

        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        int width = getWidth();
        int height = getHeight();

        if (newX < 0) {
            newX = width + newX;
        } else if (newX > width) {
            newX = newX - width;
        }

        if (newY < 0) {
            newY = height + newY;
        } else if (newY > height) {
            newY = newY - height;
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = normalizeAngle(m_robotDirection + angularVelocity * duration);
    }

    private double applyLimits(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double normalizeAngle(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform transform = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(transform);

        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);

        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);

        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);

        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform transform = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(transform);

        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);

        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void fillOval(Graphics g, int centerX, int centerY, int width, int height) {
        g.fillOval(centerX - width / 2, centerY - height / 2, width, height);
    }

    private void drawOval(Graphics g, int centerX, int centerY, int width, int height) {
        g.drawOval(centerX - width / 2, centerY - height / 2, width, height);
    }
}