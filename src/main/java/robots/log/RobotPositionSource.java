package robots.log;

import java.util.ArrayList;
import java.util.Collections;

public class RobotPositionSource {
    private final ArrayList<RobotPositionListener> listeners = new ArrayList<>();
    private volatile RobotPositionListener[] activeListeners;

    private double x = 0;
    private double y = 0;

    public synchronized void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        notifyListeners();
    }

    public synchronized double getX() {
        return x;
    }

    public synchronized double getY() {
        return y;
    }

    public void registerListener(RobotPositionListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    public void unregisterListener(RobotPositionListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

    private void notifyListeners() {
        RobotPositionListener[] currentListeners = activeListeners;
        if (currentListeners == null) {
            synchronized (listeners) {
                if (activeListeners == null) {
                    activeListeners = listeners.toArray(new RobotPositionListener[0]);
                    currentListeners = activeListeners;
                }
            }
        }

        for (RobotPositionListener listener : currentListeners) {
            listener.onPositionChanged(x, y);
        }
    }
}
