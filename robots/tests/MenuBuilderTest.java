package tests;

import src.gui.MainApplicationFrame;
import org.junit.jupiter.api.Test;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class MenuBuilderTest {

    @Test
    void testFileMenuExists() {
        MainApplicationFrame frame = new MainApplicationFrame();
        JMenuBar menuBar = frame.getJMenuBar();

        JMenu fileMenu = menuBar.getMenu(0);
        assertNotNull(fileMenu);
        assertEquals("Файл", fileMenu.getText());
    }
}