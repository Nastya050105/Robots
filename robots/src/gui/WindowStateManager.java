//отвечает за сохранение и восстановление состояния окон
package robots.src.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WindowStateManager {
    private static final String CONFIG_FILE = System.getProperty("user.home") + "/window_state.txt";

    public static void saveState(Savable[] windows) {
        Map<String, String> state = new HashMap<>();
        for (Savable window : windows) {
            window.saveState(state);
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : state.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            Files.write(Paths.get(CONFIG_FILE), sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restoreState(Savable[] windows) {
        try {
            Map<String, String> state = new HashMap<>();
            for (String line : Files.readAllLines(Paths.get(CONFIG_FILE))) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    state.put(parts[0], parts[1]);
                }
            }
            for (Savable window : windows) {
                window.restoreState(state);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}