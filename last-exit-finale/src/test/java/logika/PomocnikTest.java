package logika;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PomocnikTest {
    @Test
    void rozdelujePrikazBezArgumentu() {
        Pomocnik.Prikaz prikaz = Pomocnik.rozdelPrikaz("  MAPA   ");
        assertEquals("mapa", prikaz.nazov());
        assertEquals("", prikaz.argument());
    }

    @Test
    void rozdelujePrikazSAArgumentom() {
        Pomocnik.Prikaz prikaz = Pomocnik.rozdelPrikaz("Chod do riadiaceho centra");
        assertEquals("chod", prikaz.nazov());
        assertEquals("do riadiaceho centra", prikaz.argument());
    }

    @Test
    void prazdnyVstupJeBezpecny() {
        Pomocnik.Prikaz prazdny = Pomocnik.rozdelPrikaz("   ");
        assertEquals("", prazdny.nazov());
        assertEquals("", prazdny.argument());
    }

    @Test
    void nullVstupJeBezpecny() {
        Pomocnik.Prikaz prazdny = Pomocnik.rozdelPrikaz(null);
        assertEquals("", prazdny.nazov());
        assertEquals("", prazdny.argument());
    }
}
