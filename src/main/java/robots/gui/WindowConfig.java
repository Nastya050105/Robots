package robots.gui;

import java.io.File;

public class WindowConfig {
    public static File getConfigFile() {
        return new File(System.getProperty("user.home"), ".robot-windows.config");
    }
}
