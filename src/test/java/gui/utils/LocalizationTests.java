package gui.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class LocalizationTests {

    private Locale defaultLocale;

    @BeforeEach
    void setUp() {
        defaultLocale = Localization.getLocale();
    }

    @AfterEach
    void tearDown() {
        Localization.setLocale(defaultLocale);
    }

    @Test
    void testDefaultLocaleNotNull() {
        assertNotNull(Localization.getLocale());
    }

    @Test
    void testGetValueReturnsCorrectStringForRussian() {
        Localization.setLocale(Locale.of("ru", "RU"));
        String value = Localization.getValue("menu.exit");

        assertEquals("Выход", value);
    }

    @Test
    void testGetValueReturnsCorrectStringForEnglish() {
        Localization.setLocale(Locale.ENGLISH);
        String value = Localization.getValue("menu.exit");

        assertEquals("Exit", value);
    }

    @Test
    void testLocaleSwitchingActuallyChangesReturnedValue() {
        Localization.setLocale(Locale.of("ru", "RU"));
        String ruVal = Localization.getValue("menu.exit");

        Localization.setLocale(Locale.ENGLISH);
        String enVal = Localization.getValue("menu.exit");

        assertNotEquals(ruVal, enVal);
        assertEquals("Выход", ruVal);
        assertEquals("Exit", enVal);
    }

    @Test
    void testGetLocaleReturnsTheCorrectLocale() {
        Localization.setLocale(Locale.ENGLISH);
        assertEquals(Locale.ENGLISH, Localization.getLocale());
    }

    @Test
    void testGetValueThrowsExceptionForMissingKey() {
        Localization.setLocale(Locale.ENGLISH);

        assertThrows(Exception.class, () -> Localization.getValue("non.existent.key"));
    }
}
