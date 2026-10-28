package logika;

import logika.miestnosti.Miestnost;
import logika.predmety.Predmet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MiestnostTest {

    private Miestnost miestnost;
    private Predmet skrutkovac;
    private Predmet karta;

    @BeforeEach
    void setUp() {
        miestnost = new Miestnost("Laboratórium", "Veľká miestnosť s mikroskopmi.", "Môžeš ísť na chodbu.");
        skrutkovac = new Predmet(Predmet.SKRUTKOVAC, "Obyčajný skrutkovač.");
        karta = new Predmet(Predmet.KARTA, "Karta na otváranie dverí.");
    }

    @Test
    void konstruktorUlozNazovPopisAMoznosti() {
        assertEquals("Laboratórium", miestnost.getNazov());
        assertEquals("Veľká miestnosť s mikroskopmi.", miestnost.getPopis());
        assertEquals("Môžeš ísť na chodbu.", miestnost.getMoznosti());
    }

    @Test
    void novaMiestnostNemaZiadnePredmety() {
        assertTrue(miestnost.getPredmety().isEmpty());
    }

    @Test
    void pridajPredmetHoPridaDoZoznamu() {
        miestnost.pridajPredmet(skrutkovac);

        List<Predmet> predmety = miestnost.getPredmety();
        assertEquals(1, predmety.size());
        assertSame(skrutkovac, predmety.get(0));
    }

    @Test
    void pridanieViacPredmetovZachovaPoradie() {
        miestnost.pridajPredmet(skrutkovac);
        miestnost.pridajPredmet(karta);

        List<Predmet> predmety = miestnost.getPredmety();
        assertEquals(2, predmety.size());
        assertSame(skrutkovac, predmety.get(0));
        assertSame(karta, predmety.get(1));
    }

    @Test
    void odoberPredmetHoOdstraniZoZoznamu() {
        miestnost.pridajPredmet(skrutkovac);
        miestnost.pridajPredmet(karta);

        miestnost.odoberPredmet(skrutkovac);

        List<Predmet> predmety = miestnost.getPredmety();
        assertEquals(1, predmety.size());
        assertSame(karta, predmety.get(0));
    }

    @Test
    void odobratieNeexistujucehoPredmetuNespadne() {
        miestnost.pridajPredmet(karta);

        assertDoesNotThrow(() -> miestnost.odoberPredmet(skrutkovac));
        assertEquals(1, miestnost.getPredmety().size());
    }

    @Test
    void getPredmetyVracaKopiuZoznamu() {
        miestnost.pridajPredmet(skrutkovac);

        List<Predmet> kopia = miestnost.getPredmety();
        kopia.add(karta);
        kopia.clear();

        assertEquals(1, miestnost.getPredmety().size());
    }

    @Test
    void najdiPredmetNajdePodlaCelehoNazvu() {
        miestnost.pridajPredmet(skrutkovac);

        assertSame(skrutkovac, miestnost.najdiPredmet("Skrutkovač"));
    }

    @Test
    void najdiPredmetIgnorujeDiakritikuAVelkostPismen() {
        miestnost.pridajPredmet(skrutkovac);

        assertSame(skrutkovac, miestnost.najdiPredmet("SKRUTKOVAC"));
    }

    @Test
    void najdiPredmetNajdePodlaCastiNazvu() {
        miestnost.pridajPredmet(skrutkovac);
        miestnost.pridajPredmet(karta);

        assertSame(karta, miestnost.najdiPredmet("pristup"));
    }

    @Test
    void najdiPredmetVratiNullKedNicNenajde() {
        miestnost.pridajPredmet(skrutkovac);

        assertNull(miestnost.najdiPredmet("xyz"));
    }

    @Test
    void najdiPredmetVratiNullPreKratkyText() {
        miestnost.pridajPredmet(skrutkovac);

        // Pomocnik.obsahuje berie len texty dlhé aspoň 3 znaky.
        assertNull(miestnost.najdiPredmet("sk"));
    }

    @Test
    void najdiPredmetVPrazdnejMiestnostiVratiNull() {
        assertNull(miestnost.najdiPredmet("skrutkovac"));
    }

    @Test
    void najdiPredmetNenajdeOdobratyPredmet() {
        miestnost.pridajPredmet(skrutkovac);
        miestnost.odoberPredmet(skrutkovac);

        assertNull(miestnost.najdiPredmet("skrutkovac"));
    }
}