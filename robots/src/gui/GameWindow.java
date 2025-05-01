package robots.src.gui;

import log.Logger;

import java.awt.*;
import javax.swing.*;
import java.util.Map;


public class GameWindow extends JInternalFrame implements Savable {
    private final GameVisualizer m_visualizer;

    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);
        this.m_visualizer = new GameVisualizer(model);
        new RobotController(model, m_visualizer);

        RobotController controller = new RobotController(model, m_visualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void saveState(Map<String, String> state) {
        state.put("game.x", Integer.toString(getX()));
        state.put("game.y", Integer.toString(getY()));
        state.put("game.width", Integer.toString(getWidth()));
        state.put("game.height", Integer.toString(getHeight()));
        state.put("game.isMaximized", Boolean.toString(isMaximum()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        int x = Integer.parseInt(state.getOrDefault("game.x", "100"));
        int y = Integer.parseInt(state.getOrDefault("game.y", "100"));
        int width = Integer.parseInt(state.getOrDefault("game.width", "400"));
        int height = Integer.parseInt(state.getOrDefault("game.height", "400"));
        setBounds(x, y, width, height);

        try {
            setMaximum(Boolean.parseBoolean(state.getOrDefault("game.isMaximized", "false")));
        } catch (Exception e) {
            Logger.error("Ошибка восстановления состояния окна: " + e.getMessage());
        }
    }
}
