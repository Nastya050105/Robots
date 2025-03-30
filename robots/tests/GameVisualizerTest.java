package tests;

import src.gui.GameVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GameVisualizerTest {

    private GameVisualizer visualizer;

    @BeforeEach
    void setUp() {
        visualizer = new GameVisualizer();
    }

    @Test
    void testRobotMovesToTarget() {
        visualizer.setTargetPosition(new Point(200, 200));
        visualizer.onModelUpdateEvent();

        assertNotEquals(100, visualizer.getM_robotPositionX());
        assertNotEquals(100, visualizer.getM_robotPositionY());

        for (int i = 0; i < 1000; i++) {
            visualizer.onModelUpdateEvent();
        }


        assertEquals(200, visualizer.getM_robotPositionX(), 1.0);
        assertEquals(200, visualizer.getM_robotPositionY(), 1.0);
    }
}