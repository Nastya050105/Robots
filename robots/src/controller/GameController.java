package controller;

import model.RobotModel;
import view.GameVisualizer;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private final RobotModel model;
    private final GameVisualizer view;
    private final Timer timer;

    public GameController(RobotModel model, GameVisualizer view) {
        this.model = model;
        this.view = view;
        this.timer = new Timer("events generator", true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.updateModel();
                view.repaint();
            }
        }, 0, 10);
    }
}