package robots.src.gui;

import log.Logger;

import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RobotCoordinatesWindow extends JInternalFrame implements Observer, Savable {
    private final JLabel coordinatesLabel;
    private final RobotModel robotModel;

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.robotModel = model;
        this.robotModel.addObserver(this);

        coordinatesLabel = new JLabel("X: 0, Y: 0", SwingConstants.CENTER);
        coordinatesLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(coordinatesLabel, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        updateCoordinates();
    }

    private void updateCoordinates() {
        coordinatesLabel.setText(String.format("X: %.2f, Y: %.2f",
                robotModel.getRobotPositionX(),
                robotModel.getRobotPositionY()));
    }

    @Override
    public void update(Observable o, Object arg) {
        updateCoordinates();
    }

    @Override
    public void saveState(Map<String, String> state) {
        state.put("coord.x", Integer.toString(getX()));
        state.put("coord.y", Integer.toString(getY()));
        state.put("coord.width", Integer.toString(getWidth()));
        state.put("coord.height", Integer.toString(getHeight()));
        state.put("coord.isMaximized", Boolean.toString(isMaximum()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        int x = Integer.parseInt(state.getOrDefault("coord.x", "700"));
        int y = Integer.parseInt(state.getOrDefault("coord.y", "10"));
        int width = Integer.parseInt(state.getOrDefault("coord.width", "200"));
        int height = Integer.parseInt(state.getOrDefault("coord.height", "100"));
        setBounds(x, y, width, height);

        try {
            setMaximum(Boolean.parseBoolean(state.getOrDefault("coord.isMaximized", "false")));
        } catch (Exception e) {
            Logger.error("Ошибка восстановления состояния окна: " + e.getMessage());
        }
    }
}