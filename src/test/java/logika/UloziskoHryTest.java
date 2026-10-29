package logika;

import logika.miestnosti.TvorcaMiestnosti;
import logika.predmety.Predmet;
import logika.predmety.TvorcaPredmetov;
import logika.ukladanie.UlozenyStavHry;
import logika.ukladanie.UloziskoHry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class UloziskoHryTest {

    @TempDir
    Path tempDir;

    private Path subor;
    private UloziskoHry ulozisko;

    @BeforeEach
    void setUp() {
        subor = tempDir.resolve("test_save.properties");
        ulozisko = new UloziskoHry(subor);
    }

    // ---------- pomocné metódy ----------

    /** Vytvorí platné dáta uloženia, jednotlivé testy z nich niečo pokazia. */
    private Properties platneData() {
        Properties p = new Properties();
        p.setProperty("player.room", "1");
        p.setProperty("player.items", Predmet.SKRUTKOVAC + "|" + Predmet.KARTA);
        p.setProperty("timer.remainingSeconds", "10");
        p.setProperty("code.laboratory", "1111");
        p.setProperty("code.security", "2222");
        p.setProperty("code.final", "3333");
        p.setProperty("door.0.open", "true");
        p.setProperty("door.1.open", "false");
        p.setProperty("lock.deactivated", "true");
        p.setProperty("servicePanel.open", "false");
        p.setProperty("cooling.repaired", "true");
        p.setProperty("card.verified", "false");
        p.setProperty("finalCode.verified", "true");
        p.setProperty("notes.read", "false");
        p.setProperty("scientist.dialogFinished", "true");
        p.setProperty("room.0.items", Predmet.POZNAMKY);
        p.setProperty("room.1.items", "");
        p.setProperty("room.2.items", Predmet.VZORKA);
        return p;
    }

    private void zapis(Properties data) throws IOException {
        try (OutputStream out = Files.newOutputStream(subor)) {
            data.store(out, "test");
        }
    }

    // ---------- cesta, existuje, vymaz ----------

    @Test
    void getCestaVratiCestuZKonstruktora() {
        assertEquals(subor, ulozisko.getCesta());
    }

    @Test
    void vychodzieUloziskoPouzivaPredvolenyNazovSuboru() {
        UloziskoHry predvolene = new UloziskoHry();

        assertEquals("last_exit_save.properties", predvolene.getCesta().getFileName().toString());
    }

    @Test
    void existujeVratiFalseKedSuborNieJe() {
        assertFalse(ulozisko.existuje());
    }

    @Test
    void existujeVratiTrueKedSuborJe() throws IOException {
        zapis(platneData());

        assertTrue(ulozisko.existuje());
    }

    @Test
    void vymazOdstraniSubor() throws IOException {
        zapis(platneData());

        ulozisko.vymaz();

        assertFalse(ulozisko.existuje());
    }

    @Test
    void vymazNespadneKedSuborNeexistuje() {
        assertDoesNotThrow(() -> ulozisko.vymaz());
    }

    // ---------- nacitaj: platné uloženie ----------

    @Test
    void nacitajVratiVsetkyHodnotyZPlatnehoSuboru() throws IOException {
        zapis(platneData());

        UlozenyStavHry stav = ulozisko.nacitaj();

        assertEquals(1, stav.miestnostHraca());
        assertEquals(List.of(Predmet.SKRUTKOVAC, Predmet.KARTA), stav.predmetyHraca());
        assertEquals(10, stav.zostavajuceSekundy());
        assertEquals(1111, stav.kodLaboratorium());
        assertEquals(2222, stav.kodZamok());
        assertEquals(3333, stav.kodFinalny());
    }

    @Test
    void priradiPravdivostneHodnoty() throws IOException {
        zapis(platneData());

        UlozenyStavHry stav = ulozisko.nacitaj();

        assertTrue(stav.dvereLaboratoriumOtvorene());
        assertFalse(stav.dvereKomoraOtvorene());
        assertTrue(stav.zamokDeaktivovany());
        assertFalse(stav.servisnyPanelOtvoreny());
        assertTrue(stav.chladenieOpravene());
        assertFalse(stav.kartaOverena());
        assertTrue(stav.finalnyKodOvereny());
        assertFalse(stav.poznamkyPrecitane());
        assertTrue(stav.dialogDokonceny());
    }

    @Test
    void nacitajVratiPredmetyVsetkychTrochMiestnosti() throws IOException {
        zapis(platneData());

        List<List<String>> miestnosti = ulozisko.nacitaj().predmetyMiestnosti();

        assertEquals(3, miestnosti.size());
        assertEquals(List.of(Predmet.POZNAMKY), miestnosti.get(0));
        assertTrue(miestnosti.get(1).isEmpty());
        assertEquals(List.of(Predmet.VZORKA), miestnosti.get(2));
    }

    @Test
    void nacitajZvladnePrazdnyZoznamPredmetovHraca() throws IOException {
        Properties data = platneData();
        data.setProperty("player.items", "");
        zapis(data);

        assertTrue(ulozisko.nacitaj().predmetyHraca().isEmpty());
    }

    @Test
    void nacitajZvladneChybajuciZoznamPredmetovMiestnosti() throws IOException {
        Properties data = platneData();
        data.remove("room.1.items");
        zapis(data);

        assertTrue(ulozisko.nacitaj().predmetyMiestnosti().get(1).isEmpty());
    }

    @Test
    void nacitajBooleanNezalezIVelkostPismen() throws IOException {
        Properties data = platneData();
        data.setProperty("door.1.open", "TRUE");
        zapis(data);

        assertTrue(ulozisko.nacitaj().dvereKomoraOtvorene());
    }

    @Test
    void nacitajPrijmeHranicneMiestnosti() throws IOException {
        for (String miestnost : new String[]{"0", "2"}) {
            Properties data = platneData();
            data.setProperty("player.room", miestnost);
            zapis(data);

            assertEquals(Integer.parseInt(miestnost), ulozisko.nacitaj().miestnostHraca());
        }
    }

    // ---------- nacitaj: neplatné uloženie ----------

    @Test
    void nacitajChybiSuborVyhodiIOException() {
        assertThrows(IOException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneMiestnostMimoRozsah() throws IOException {
        for (String zla : new String[]{"-1", "3"}) {
            Properties data = platneData();
            data.setProperty("player.room", zla);
            zapis(data);

            assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj(), "miestnosť " + zla);
        }
    }

    @Test
    void nacitajOdmietneZapornyCas() throws IOException {
        Properties data = platneData();
        data.setProperty("timer.remainingSeconds", "-1");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneCasNadLimit() throws IOException {
        Properties data = platneData();
        data.setProperty("timer.remainingSeconds", "999999999");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneKodMimoStvorcisla() throws IOException {
        for (String zlyKod : new String[]{"999", "10000"}) {
            Properties data = platneData();
            data.setProperty("code.security", zlyKod);
            zapis(data);

            assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj(), "kód " + zlyKod);
        }
    }

    @Test
    void nacitajOdmietneOpakujuceSaKody() throws IOException {
        Properties data = platneData();
        data.setProperty("code.final", "1111");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneChybajucuPovinnuHodnotu() throws IOException {
        Properties data = platneData();
        data.remove("player.room");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneChybajuciZoznamPredmetovHraca() throws IOException {
        Properties data = platneData();
        data.remove("player.items");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneCisloKtoreNieJeCislo() throws IOException {
        Properties data = platneData();
        data.setProperty("player.room", "abc");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneNeplatnuBooleanHodnotu() throws IOException {
        Properties data = platneData();
        data.setProperty("lock.deactivated", "mozno");
        zapis(data);

        assertThrows(IllegalArgumentException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void nacitajOdmietneNeznamyPredmet() throws IOException {
        Properties data = platneData();
        data.setProperty("player.items", "Neexistujuci predmet");
        zapis(data);

        assertThrows(RuntimeException.class, () -> ulozisko.nacitaj());
    }

    @Test
    void ulozPotomNacitajZachovaStavHry() throws IOException {
        HerniStav povodny = new HerniStav();
        povodny.novaHra(new TvorcaMiestnosti());

        povodny.getHrac().pridajPredmet(
                TvorcaPredmetov.vytvor(Predmet.SKRUTKOVAC)
        );

        ulozisko.uloz(povodny);

        UlozenyStavHry nacitany = ulozisko.nacitaj();

        assertEquals(
                povodny.getKodLaboratorium(),
                nacitany.kodLaboratorium()
        );
        assertEquals(
                povodny.getKodZamok(),
                nacitany.kodZamok()
        );
        assertEquals(
                povodny.getKodFinalny(),
                nacitany.kodFinalny()
        );

        assertEquals(
                List.of(Predmet.SKRUTKOVAC),
                nacitany.predmetyHraca()
        );
    }
}