package gui;

import java.util.HashMap;

public interface Savable {
    HashMap<String, Integer> saveState();

    void loadState(HashMap<String, Integer> state);
}