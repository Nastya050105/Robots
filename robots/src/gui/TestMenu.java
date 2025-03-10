package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import log.Logger;

public class TestMenu extends JMenu {
    public TestMenu() {
        super("Тесты");
        setMnemonic(KeyEvent.VK_T);
        getAccessibleContext().setAccessibleDescription("Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((_) -> Logger.debug("Новая строка"));
        add(addLogMessageItem);
    }
}
