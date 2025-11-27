package gui.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private static Locale locale = Locale.getDefault();
    private static ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String getValue(String key) {
        return bundle.getString(key);
    }

    public static Locale getLocale() {
        return locale;
    }
}