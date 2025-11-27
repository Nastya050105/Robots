package gui.utils;

import javax.swing.*;

public class OnExitPopup {
    static {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "ОК");
    }

    private OnExitPopup() {
    }

    public static void show(JFrame parentComponent, Runnable onExit) {
        int result = JOptionPane.showConfirmDialog(
                parentComponent,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            onExit.run();

            parentComponent.dispose();
            System.exit(0);
        }
    }
}
