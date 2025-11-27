package gui.models;

import java.util.Observable;

public class Robot extends Observable {
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private double targetX = 150;
    private double targetY = 100;

    public synchronized double getX() {
        return x;
    }

    public synchronized double getY() {
        return y;
    }

    public synchronized double getDirection() {
        return direction;
    }

    public synchronized void setTarget(double tx, double ty) {
        targetX = tx;
        targetY = ty;
    }

    public synchronized void update(double dt) {
        double dist = distance(x, y, targetX, targetY);
        if (dist < 0.5) return;

        double angleToTarget = angleTo(x, y, targetX, targetY);
        double angleDiff = shortestAngle(direction, angleToTarget);

        double angularVelocity = applyLimits(angleDiff / 10.0, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        moveRobot(MAX_VELOCITY, angularVelocity, dt);

        setChanged();
        notifyObservers();
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = x + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
        if (!Double.isFinite(newX)) {
            newX = x + velocity * duration * Math.cos(direction);
        }

        double newY = y - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = y + velocity * duration * Math.sin(direction);
        }

        x = newX;
        y = newY;
        direction = asNormalizedRadians(direction + angularVelocity * duration);
    }

    private static double applyLimits(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    private static double shortestAngle(double from, double to) {
        double diff = to - from;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        return diff;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}