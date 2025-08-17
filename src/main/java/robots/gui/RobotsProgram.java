package robots.gui;

import robots.log.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Locale;

public class RobotsProgram
{
    public static void main(String[] args) {
      Locale.setDefault(new Locale("ru", "RU"));

      try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      } catch (Exception e) {
        Logger.error("Ошибка при установке LookAndFeel: " + e.getMessage());
      }

      SwingUtilities.invokeLater(() -> {
        new MainController().createFrame();
      });

    }
}
