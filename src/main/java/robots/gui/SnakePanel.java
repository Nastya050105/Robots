package robots.gui;

import robots.log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

public class SnakePanel extends JPanel {
    private static final double LEADER_SPEED = 1.5;
    private static final double FOLLOWER_SPEED = 0.8; // медленнее

    private int lives = 3;
    private static final int MAX_LIVES = 5;
    private static final double CATCH_DISTANCE = 10.0; // если ближе — атака

    private double snakeX = 100;
    private double snakeY = 100;
    private double direction = 0;

    private double followerX = 50;
    private double followerY = 50;
    private double followerDirection = 0;

    private int targetX = 150;
    private int targetY = 100;

    private static final double SPEED = 1.5;
    private static final double ROTATION_SPEED = 0.05;

    public SnakePanel() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                targetX = e.getX();
                targetY = e.getY();
                SwingUtilities.invokeLater(() -> {
                    repaint();
                });

            }
        });

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateModel();
                SwingUtilities.invokeLater(() -> {
                    repaint();
                });            }
        }, 0, 10);
    }

    private void updateModel() {
        // === ОСНОВНАЯ ЗМЕЯ ===
        double angleToTarget = Math.atan2(targetY - snakeY, targetX - snakeX);
        double angleDiff = normalizeAngle(angleToTarget - direction);

        if (Math.abs(angleDiff) > ROTATION_SPEED) {
            direction += Math.signum(angleDiff) * ROTATION_SPEED;
        } else {
            direction = angleToTarget;
        }

        double dx = LEADER_SPEED * Math.cos(direction);
        double dy = LEADER_SPEED * Math.sin(direction);

        if (distance(snakeX, snakeY, targetX, targetY) > SPEED) {
            snakeX += dx;
            snakeY += dy;
        }

        Logger.getrobotPositionModel().setPosition(snakeX, snakeY);

        // === ВТОРАЯ ЗМЕЯ: ПРЕСЛЕДОВАТЕЛЬ ===
        double angleToLeader = Math.atan2(snakeY - followerY, snakeX - followerX);
        double followerAngleDiff = normalizeAngle(angleToLeader - followerDirection);

        if (Math.abs(followerAngleDiff) > ROTATION_SPEED) {
            followerDirection += Math.signum(followerAngleDiff) * ROTATION_SPEED;
        } else {
            followerDirection = angleToLeader;
        }

        double followerDx = FOLLOWER_SPEED * Math.cos(followerDirection);
        double followerDy = FOLLOWER_SPEED * Math.sin(followerDirection);

        if (distance(followerX, followerY, snakeX, snakeY) > SPEED) {
            followerX += followerDx;
            followerY += followerDy;
        }

        // если змея добралась до цели
        if (distance(snakeX, snakeY, targetX, targetY) <= LEADER_SPEED) {
            if (lives < MAX_LIVES) lives++;
            // можно обновить
        }

        if (distance(followerX, followerY, snakeX, snakeY) < CATCH_DISTANCE) {
            if (lives > 0) lives--;
        }

    }

    private double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSnake((Graphics2D) g, snakeX, snakeY, direction, Color.MAGENTA);
        drawSnake((Graphics2D) g, followerX, followerY, followerDirection, Color.BLUE);
        drawTarget((Graphics2D) g);

        drawLives((Graphics2D) g);
    }

    private void drawLives(Graphics2D g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Lives: " + "❤️".repeat(lives), 10, 20);
    }


    private void drawSnake(Graphics2D g, double x, double y, double dir, Color color) {
        int bodyLength = 30;
        int bodyWidth = 10;

        AffineTransform old = g.getTransform();
        g.rotate(dir, x, y);

        g.setColor(color);
        g.fillOval((int)(x - bodyLength / 2), (int)(y - bodyWidth / 2), bodyLength, bodyWidth);
        g.setColor(Color.BLACK);
        g.drawOval((int)(x - bodyLength / 2), (int)(y - bodyWidth / 2), bodyLength, bodyWidth);

        g.setTransform(old);
    }


    private void drawTarget(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillOval(targetX - 4, targetY - 4, 8, 8);
        g.setColor(Color.BLACK);
        g.drawOval(targetX - 4, targetY - 4, 8, 8);
    }

}
