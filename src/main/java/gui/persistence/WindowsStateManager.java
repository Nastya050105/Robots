package gui.persistence;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WindowsStateManager {
    private final File configFile;

    public WindowsStateManager(String dirToSave) {
        configFile = new File(dirToSave, "windows.properties");
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
    }

    public void save(JDesktopPane desktopPane) {
        Properties props = new Properties();

        JInternalFrame[] frames = desktopPane.getAllFrames();

        for (JInternalFrame f : frames) {
            String key = f.getClass().getSimpleName();

            Rectangle bounds = f.getBounds();
            props.setProperty(key + ".x", String.valueOf(bounds.x));
            props.setProperty(key + ".y", String.valueOf(bounds.y));
            props.setProperty(key + ".w", String.valueOf(bounds.width));
            props.setProperty(key + ".h", String.valueOf(bounds.height));

            props.setProperty(key + ".icon", String.valueOf(f.isIcon()));
            props.setProperty(key + ".max", String.valueOf(f.isMaximum()));
        }

        try (FileOutputStream out = new FileOutputStream(configFile)) {
            props.store(out, "Window state");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(JDesktopPane desktopPane) {
        if (!configFile.exists()) return;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JInternalFrame[] frames = desktopPane.getAllFrames();

        for (JInternalFrame f : frames) {
            String key = f.getClass().getSimpleName();

            try {
                int x = parseIntProperty(props, key + ".x", f.getX());
                int y = parseIntProperty(props, key + ".y", f.getY());
                int w = parseIntProperty(props, key + ".w", f.getWidth());
                int h = parseIntProperty(props, key + ".h", f.getHeight());

                f.setBounds(x, y, w, h);

                boolean icon = parseBoolProperty(props, key + ".icon");
                boolean max = parseBoolProperty(props, key + ".max");

                if (icon) f.setIcon(true);
                if (max) f.setMaximum(true);

            } catch (Exception e) {
                e.printStackTrace();
                // На случай отсутствия данных или ошибок
            }
        }
    }

    private static int parseIntProperty(Properties props, String prop, int fallback) {
        String value = props.getProperty(prop);
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return Integer.parseInt(value);
    }

    private static boolean parseBoolProperty(Properties props, String prop) {
        String value = props.getProperty(prop);
        if (value == null || value.isBlank()) {
            return false;
        }

        return Boolean.parseBoolean(value);
    }
}
