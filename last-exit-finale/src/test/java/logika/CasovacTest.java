package logika;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasovacTest {

    @Test
    void nastavenieNulovehoCasuNastaviZostavajuciCasNaNulu() {
        Casovac casovac = new Casovac();

        casovac.nastavZostavajuceSekundy(0);

        Assertions.assertEquals(0, casovac.zostavaSekund());
        Assertions.assertTrue(casovac.uplynul());
        Assertions.assertEquals("00:00", casovac.format());
    }

    @Test
    void pozastavenyCasovacNeuplynieAniPoNulovomCase() {
        Casovac casovac = new Casovac();
        casovac.nastavZostavajuceSekundy(0);

        casovac.pozastav();

        Assertions.assertFalse(casovac.uplynul());
        Assertions.assertEquals(0, casovac.zostavaSekund());

        casovac.pokracuj();

        Assertions.assertTrue(casovac.uplynul());
    }

    @Test
    void pozastavenyCasovacNemeniZostavajuciCas() throws InterruptedException {
        Casovac casovac = new Casovac();
        casovac.nastavZostavajuceSekundy(100);

        casovac.pozastav();
        long predCakanim = casovac.zostavaSekund();
        Thread.sleep(1200);
        long poCakani = casovac.zostavaSekund();

        assertEquals(predCakanim, poCakani);
        assertTrue(predCakanim >= 99 && predCakanim <= 100);
        Assertions.assertFalse(casovac.uplynul());
    }

    @Test
    void pokracujObnoviOdpocitavanieOdCasuZPauzy() throws InterruptedException {
        Casovac casovac = new Casovac();
        casovac.nastavZostavajuceSekundy(100);
        casovac.pozastav();
        long zostavaPriPauze = casovac.zostavaSekund();
        Thread.sleep(1200);

        casovac.pokracuj();
        Thread.sleep(1200);
        long poObnoveni = casovac.zostavaSekund();

        assertTrue(poObnoveni < zostavaPriPauze,
                "Po obnovení musí čas znovu ubúdať.");
        assertTrue(poObnoveni >= zostavaPriPauze - 3,
                "Čas z pauzy sa nesmie stratiť.");
        Assertions.assertFalse(casovac.uplynul());
    }

    @Test
    void viacnasobnePozastavenieNezmeniZostavajuciCas() throws InterruptedException {
        Casovac casovac = new Casovac();
        casovac.nastavZostavajuceSekundy(100);

        casovac.pozastav();
        long prve = casovac.zostavaSekund();
        Thread.sleep(1100);
        casovac.pozastav();

        Assertions.assertEquals(prve, casovac.zostavaSekund());
    }

    @Test
    void zaporneSekundySaOrezuNaNulu() {
        Casovac casovac = new Casovac();

        casovac.nastavZostavajuceSekundy(-50);

        Assertions.assertEquals(0, casovac.zostavaSekund());
        Assertions.assertEquals("00:00", casovac.format());
    }

    @Test
    void prilisVelkeSekundySaOrezuNaLimit() {
        Casovac casovac = new Casovac();

        casovac.nastavZostavajuceSekundy(Casovac.getLimitSekundy() + 1000);

        long zostava = casovac.zostavaSekund();
        assertTrue(zostava <= Casovac.getLimitSekundy());
        assertTrue(zostava >= Casovac.getLimitSekundy() - 1);
    }

    @Test
    void limitJePatnastMinut() {
        Assertions.assertEquals(15 * 60, Casovac.getLimitSekundy());
    }

    @Test
    void formatVracaMinutyASekundyVoFormateMMSS() {
        Casovac casovac = new Casovac();

        casovac.nastavZostavajuceSekundy(581);

        String format = casovac.format();
        assertTrue(format.equals("09:41") || format.equals("09:40"),
                "Očakávaný formát 09:41, ale bolo: " + format);
        assertTrue(format.matches("\\d{2}:\\d{2}"));
    }

    @Test
    void formatDoplniNulyPriMalychHodnotach() {
        Casovac casovac = new Casovac();

        casovac.nastavZostavajuceSekundy(5);

        String format = casovac.format();
        assertTrue(format.equals("00:05") || format.equals("00:04"),
                "Očakávaný formát 00:05, ale bolo: " + format);
    }

    @Test
    void formatNovehoCasovacaJeNaLimite() {
        Casovac casovac = new Casovac();

        String format = casovac.format();

        assertTrue(format.equals("15:00") || format.equals("14:59"),
                "Očakávaný formát 15:00, ale bolo: " + format);
    }

    @Test
    void restartVratiCasovacNaPlnyLimitAZrusiPauzu() {
        Casovac casovac = new Casovac();
        casovac.nastavZostavajuceSekundy(10);
        casovac.pozastav();

        casovac.restart();

        assertTrue(casovac.zostavaSekund() >= Casovac.getLimitSekundy() - 1);
        Assertions.assertFalse(casovac.uplynul());
    }
}