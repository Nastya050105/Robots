package robots.src.gui;

import java.util.Observable;

public class RobotModel extends Observable {
    private double robotPositionX = 100;
    private double robotPositionY = 100;
    private double robotDirection = 0;

    public void updatePosition(double x, double y, double direction) {
        this.robotPositionX = x;
        this.robotPositionY = y;
        this.robotDirection = direction;
        setChanged();
        notifyObservers();
    }

    public double getRobotPositionX() {
        return robotPositionX;
    }

    public double getRobotPositionY() {
        return robotPositionY;
    }

    public double getRobotDirection() { 
        return robotDirection;
    }
}
