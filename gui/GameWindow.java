package gui;

import robots.src.gui.GameVisualizer;
import robots.src.gui.PrefixedStateMap;
import robots.src.gui.Savable;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements Savable {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void saveState(Map<String, String> state) {
        state.put("game.width", String.valueOf(getWidth()));
        state.put("game.height", String.valueOf(getHeight()));
        state.put("game.x", String.valueOf(getX()));
        state.put("game.y", String.valueOf(getY()));
        state.put("game.isMaximized", String.valueOf(isMaximum()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        PrefixedStateMap gameState = new PrefixedStateMap(state, "game.");
        if (!gameState.isEmpty()) {
            setBounds(
                    Integer.parseInt(gameState.getOrDefault("x", "100")),
                    Integer.parseInt(gameState.getOrDefault("y", "100")),
                    Integer.parseInt(gameState.getOrDefault("width", "400")),
                    Integer.parseInt(gameState.getOrDefault("height", "400"))
            );
            try {
                setMaximum(Boolean.parseBoolean(gameState.get("isMaximized")));
            } catch (Exception ignored) {
            }
        }
    }
}