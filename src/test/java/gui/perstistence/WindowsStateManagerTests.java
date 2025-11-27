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
        // Given
        Path nonExistentDir = tempDir.resolve("nonexistent-" + UUID.randomUUID());

        // When
        new WindowsStateManager(nonExistentDir.toString());

        // Then
        assertTrue(nonExistentDir.toFile().exists(), "Directory should be created");
    }

    @Test
    void testSaveCreatesConfigFile(@TempDir Path tempDir) {
        // Given
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test Frame");
        desktopPane.add(frame);
        File configFile = new File(tempDir.toFile(), "windows.properties");

        // When
        manager.save(desktopPane);

        // Then
        assertTrue(configFile.exists(), "Config file should be created");
    }

    @Test
    void testSaveStoresFrameProperties(@TempDir Path tempDir) throws Exception {
        // Given
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test Frame");
        frame.setBounds(100, 150, 300, 400);
        frame.setIcon(false);
        frame.setMaximum(false);
        desktopPane.add(frame);
        File configFile = new File(tempDir.toFile(), "windows.properties");

        // When
        manager.save(desktopPane);

        // Then
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
        // Given
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> manager.load(desktopPane));
    }

    @Test
    void testLoadRestoresFrameProperties(@TempDir Path tempDir) throws PropertyVetoException {
        // Given
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());

        // Setup original state and save
        JDesktopPane originalDesktop = new JDesktopPane();
        JInternalFrame originalFrame = new JInternalFrame("Original-" + UUID.randomUUID());
        originalFrame.setBounds(100, 150, 300, 400);
        originalFrame.setIcon(true);
        originalFrame.setMaximum(false);
        originalDesktop.add(originalFrame);
        manager.save(originalDesktop);

        // When - load into new desktop
        JDesktopPane newDesktop = new JDesktopPane();
        JInternalFrame newFrame = new JInternalFrame("New-" + UUID.randomUUID());
        newDesktop.add(newFrame);
        manager.load(newDesktop);

        // Then
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
        // Given - create a corrupted config file
        File configFile = new File(tempDir.toFile(), "windows.properties");
        String corruptedContent = "JInternalFrame.x=100\nJInternalFrame.y=invalid";
        Files.writeString(configFile.toPath(), corruptedContent);

        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test-" + UUID.randomUUID());
        desktopPane.add(frame);

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> manager.load(desktopPane));
    }

    @Test
    void testLoadHandlesPartialData(@TempDir Path tempDir) throws IOException {
        // Given - config with only some properties
        File configFile = new File(tempDir.toFile(), "windows.properties");
        String partialContent = "JInternalFrame.x=150\nJInternalFrame.y=200";
        Files.writeString(configFile.toPath(), partialContent);

        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame frame = new JInternalFrame("Test-" + UUID.randomUUID());
        frame.setBounds(0, 0, 500, 500); // initial bounds
        desktopPane.add(frame);

        // When
        manager.load(desktopPane);

        // Then - frame should keep its original size since w/h weren't in config
        Rectangle bounds = frame.getBounds();
        assertAll(
                () -> assertEquals(150, bounds.x),
                () -> assertEquals(200, bounds.y),
                () -> assertEquals(500, bounds.width), // original width
                () -> assertEquals(500, bounds.height) // original height
        );
    }

    @Test
    void testLoadWithNoFramesInDesktop(@TempDir Path tempDir) throws IOException {
        // Given - config file exists
        File configFile = new File(tempDir.toFile(), "windows.properties");
        String configContent = "JInternalFrame.x=100\nJInternalFrame.y=150";
        Files.writeString(configFile.toPath(), configContent);

        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());

        // When & Then - empty desktop pane, should not throw exception
        assertDoesNotThrow(() -> manager.load(new JDesktopPane()));
    }

    @Test
    void testSaveWithNoFrames(@TempDir Path tempDir) {
        // Given
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();
        File configFile = new File(tempDir.toFile(), "windows.properties");

        // When
        manager.save(desktopPane);

        // Then - file should be created but empty (or with only comments)
        assertTrue(configFile.exists());
    }

    @Test
    void testFrameClassNameUsedAsKey(@TempDir Path tempDir) throws Exception {
        // Given
        WindowsStateManager manager = new WindowsStateManager(tempDir.toString());
        JDesktopPane desktopPane = new JDesktopPane();

        // Create custom frame class to test class name usage
        class CustomFrame extends JInternalFrame {
            CustomFrame(String title) {
                super(title);
            }
        }

        CustomFrame customFrame = new CustomFrame("Custom");
        customFrame.setBounds(77, 88, 199, 299);
        desktopPane.add(customFrame);

        File configFile = new File(tempDir.toFile(), "windows.properties");

        // When
        manager.save(desktopPane);

        // Then - should use simple class name as key prefix
        String content = Files.readString(configFile.toPath());
        assertAll(
                () -> assertTrue(content.contains("CustomFrame.x=77")),
                () -> assertTrue(content.contains("CustomFrame.y=88")),
                () -> assertTrue(content.contains("CustomFrame.w=199")),
                () -> assertTrue(content.contains("CustomFrame.h=299"))
        );
    }
}
