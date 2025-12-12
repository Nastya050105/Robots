package gui.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RobotTests {
    @Test
    public void testRobotMovesTowardTarget() {
        Robot model = new Robot();

        double x0 = model.getX();
        double y0 = model.getY();

        model.setTarget(200, 200);

        for (int i = 0; i < 200; i++) {
            model.update(10);
        }

        assertTrue(model.getX() > x0);
        assertTrue(model.getY() > y0);
    }
}