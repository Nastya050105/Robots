package robots.src.gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Timer;
import java.util.TimerTask;

public class RobotController {
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.01;

    private final RobotModel robotModel;
    private final GameVisualizer visualizer;
    private Timer timer;

    private int targetPositionX = 150;
    private int targetPositionY = 100;

    public RobotController(RobotModel model, GameVisualizer visualizer) {
        this.robotModel = model;
        this.visualizer = visualizer;
        this.timer = new Timer("Robot controller timer", true);

        initEventHandlers();
        startUpdateTimer();
    }

    private void initEventHandlers() {
        visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
            }
        });
    }

    private void startUpdateTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRobotPosition();
                visualizer.repaint();
            }
        }, 0, 10);
    }

    public void setTargetPosition(Point p) {
        targetPositionX = p.x;
        targetPositionY = p.y;
        visualizer.setTargetPosition(p.x, p.y);
    }

    public void updateRobotPosition() {
        double distance = distance(robotModel.getRobotPositionX(), robotModel.getRobotPositionY(),
                targetPositionX, targetPositionY);
        if (distance < 0.5) {
            return;
        }

        double angleToTarget = angleTo(robotModel.getRobotPositionX(), robotModel.getRobotPositionY(),
                targetPositionX, targetPositionY);
        double angleDiff = asNormalizedRadians(angleToTarget - robotModel.getRobotDirection());

        double angularVelocity = 0;
        if (angleDiff > Math.PI) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        } else if (angleDiff > 0.1) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else if (angleDiff < -Math.PI) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else if (angleDiff < -0.1) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(MAX_VELOCITY, angularVelocity, 10);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = robotModel.getRobotPositionX() + velocity / angularVelocity *
                (Math.sin(robotModel.getRobotDirection() + angularVelocity * duration) -
                        Math.sin(robotModel.getRobotDirection()));
        if (!Double.isFinite(newX)) {
            newX = robotModel.getRobotPositionX() + velocity * duration * Math.cos(robotModel.getRobotDirection());
        }

        double newY = robotModel.getRobotPositionY() - velocity / angularVelocity *
                (Math.cos(robotModel.getRobotDirection() + angularVelocity * duration) -
                        Math.cos(robotModel.getRobotDirection()));
        if (!Double.isFinite(newY)) {
            newY = robotModel.getRobotPositionY() + velocity * duration * Math.sin(robotModel.getRobotDirection());
        }

        double newDirection = asNormalizedRadians(robotModel.getRobotDirection() + angularVelocity * duration);
        robotModel.updatePosition(newX, newY, newDirection);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        while (angle >= Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static double applyLimits(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}