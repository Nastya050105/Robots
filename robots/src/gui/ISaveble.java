package gui;

import java.util.HashMap;

public interface ISaveble {
    HashMap<String, Integer> saveState();

    void loadState(HashMap<String, Integer> state);
}