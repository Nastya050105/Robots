package gui.perstistence;

import gui.persistence.WindowsStateManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WindowsStateManagerTests {

    @Test
    void testConstructorCreatesDirectory(@TempDir Path tempDir) {
        Path nonExistentDir = tempDir.resolve("nonexistent-" + UUID.randomUUID());

        new WindowsStateManager(nonExistentDir.toString());

        assertTrue(nonExistentDir.toFile().exists(), "Directory should be created");
    }

    @Test
    void testSaveCreatesConfigFile(@TempDir Path tempDir) {
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test Frame");
        desktopPane.add(frame);
        File configFile = new File(tempDir.toFile(), "windows.properties");

        manager.save(desktopPane);

        assertTrue(configFile.exists(), "Config file should be created");
    }

    @Test
    void testSaveStoresFrameProperties(@TempDir Path tempDir) throws Exception {
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test Frame");
        frame.setBounds(100, 150, 300, 400);
        frame.setIcon(false);
        frame.setMaximum(false);
        desktopPane.add(frame);
        File configFile = new File(tempDir.toFile(), "windows.properties");

        manager.save(desktopPane);

        String content = Files.readString(configFile.toPath());
        assertAll(
                () -> assertTrue(content.contains("JInternalFrame.x=100")),
                () -> assertTrue(content.contains("JInternalFrame.y=150")),
                () -> assertTrue(content.contains("JInternalFrame.w=300")),
                () -> assertTrue(content.contains("JInternalFrame.h=400")),
                () -> assertTrue(content.contains("JInternalFrame.icon=false")),
                () -> assertTrue(content.contains("JInternalFrame.max=false"))
        );
    }

    @Test
    void testLoadWhenFileDoesNotExist(@TempDir Path tempDir) {
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();

        assertDoesNotThrow(() -> manager.load(desktopPane));
    }

    @Test
    void testLoadRestoresFrameProperties(@TempDir Path tempDir) throws PropertyVetoException {
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());

        JDesktopPane originalDesktop = new JDesktopPane();
        JInternalFrame originalFrame = new JInternalFrame("Original-" + UUID.randomUUID());
        originalFrame.setBounds(100, 150, 300, 400);
        originalFrame.setIcon(true);
        originalFrame.setMaximum(false);
        originalDesktop.add(originalFrame);
        manager.save(originalDesktop);

        JDesktopPane newDesktop = new JDesktopPane();
        JInternalFrame newFrame = new JInternalFrame("New-" + UUID.randomUUID());
        newDesktop.add(newFrame);
        manager.load(newDesktop);

        Rectangle bounds = newFrame.getBounds();
        assertAll(
                () -> assertEquals(100, bounds.x),
                () -> assertEquals(150, bounds.y),
                () -> assertEquals(300, bounds.width),
                () -> assertEquals(400, bounds.height),
                () -> assertTrue(newFrame.isIcon()),
                () -> assertFalse(newFrame.isMaximum())
        );
    }

    @Test
    void testLoadIgnoresMissingProperties(@TempDir Path tempDir) throws IOException {
        File configFile = new File(tempDir.toFile(), "windows.properties");
        String corruptedContent = "JInternalFrame.x=100\nJInternalFrame.y=invalid";
        Files.writeString(configFile.toPath(), corruptedContent);

        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test-" + UUID.randomUUID());
        desktopPane.add(frame);

        assertDoesNotThrow(() -> manager.load(desktopPane));
    }

    @Test
    void testLoadHandlesPartialData(@TempDir Path tempDir) throws IOException {
        File configFile = new File(tempDir.toFile(), "windows.properties");
        String partialContent = "JInternalFrame.x=150\nJInternalFrame.y=200";
        Files.writeString(configFile.toPath(), partialContent);

        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test-" + UUID.randomUUID());
        frame.setBounds(0, 0, 500, 500);
        desktopPane.add(frame);

        manager.load(desktopPane);

        Rectangle bounds = frame.getBounds();
        assertAll(
                () -> assertEquals(150, bounds.x),
                () -> assertEquals(200, bounds.y),
                () -> assertEquals(500, bounds.width),
                () -> assertEquals(500, bounds.height)
        );
    }

    @Test
    void testLoadWithNoFramesInDesktop(@TempDir Path tempDir) throws IOException {
        File configFile = new File(tempDir.toFile(), "windows.properties");
        String configContent = "JInternalFrame.x=100\nJInternalFrame.y=150";
        Files.writeString(configFile.toPath(), configContent);

        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());

        assertDoesNotThrow(() -> manager.load(new JDesktopPane()));
    }

    @Test
    void testSaveWithNoFrames(@TempDir Path tempDir) {
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        File configFile = new File(tempDir.toFile(), "windows.properties");

        manager.save(desktopPane);

        assertTrue(configFile.exists());
    }

    @Test
    void testFrameClassNameUsedAsKey(@TempDir Path tempDir) throws Exception {
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();

        class CustomFrame extends JInternalFrame {
            CustomFrame(String title) {
                super(title);
            }
        }

        CustomFrame customFrame = new CustomFrame("Custom");
        customFrame.setBounds(77, 88, 199, 299);
        desktopPane.add(customFrame);

        File configFile = new File(tempDir.toFile(), "windows.properties");

        manager.save(desktopPane);

        String content = Files.readString(configFile.toPath());
        assertAll(
                () -> assertTrue(content.contains("CustomFrame.x=77")),
                () -> assertTrue(content.contains("CustomFrame.y=88")),
                () -> assertTrue(content.contains("CustomFrame.w=199")),
                () -> assertTrue(content.contains("CustomFrame.h=299"))
        );
    }
}
