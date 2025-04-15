package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;

public abstract class MyWindow extends JInternalFrame implements Savable {
    private final String windowKey;
    protected int minHeight, minWidth;

    protected MyWindow(String title, String windowKey) {
        super(title, true, true, true, true);
        this.windowKey = windowKey;
    }

    @Override
    public HashMap<String, Integer> saveState() {
        try {
            if (isClosed()) {
                return new HashMap<>();
            }

            int width, height, x, y;
            boolean wasMaximized = false;

            if (isMaximum()) {
                wasMaximized = true;
                setMaximum(false);
            }

            width = getWidth();
            height = getHeight();
            x = getX();
            y = getY();

            if (wasMaximized) {
                setMaximum(true);
            }

            HashMap<String, Integer> state = new HashMap<>();
            state.put(windowKey + ".width", width);
            state.put(windowKey + ".height", height);
            state.put(windowKey + ".x", x);
            state.put(windowKey + ".y", y);
            state.put(windowKey + ".state", isIcon() ? 0 : (wasMaximized ? 2 : 1));

            return state;
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @Override
    public void loadState(HashMap<String, Integer> state) {
        try {
            if (isClosed() || state == null) {
                return;
            }

            int width = Math.max(state.getOrDefault(windowKey + ".width", getWidth()), minWidth);
            int height = Math.max(state.getOrDefault(windowKey + ".height", getHeight()), minHeight);
            int x =  state.getOrDefault(windowKey + ".x", getX());
            int y = state.getOrDefault(windowKey + ".y", getY());
            int windowState = state.getOrDefault(windowKey + ".state", 1);

            setSize(new Dimension(width, height));
            setLocation(x, y);

            if (windowState == 0) {
                setIcon(true);
            } else {
                setIcon(false);
            }

            if (windowState == 2) {
                setMaximum(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}