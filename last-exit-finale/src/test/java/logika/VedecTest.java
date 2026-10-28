package logika;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VedecTest {

    private Vedec vedec;

    @BeforeEach
    void setUp() {
        vedec = new Vedec("Dr. Novák");
    }

    @Test
    void konstruktorUlozMeno() {
        assertEquals("Dr. Novák", vedec.getMeno());
    }

    @Test
    void novyVedecNemaDokonceniDialog() {
        assertFalse(vedec.isDialogDokonceny());
    }

    @Test
    void prvyRozhovorObsahujeKodZamku() {
        String text = vedec.hovor(4821);

        assertTrue(text.contains("4821"));
    }

    @Test
    void prvyRozhovorJeUvodnyAObsahujeNavod() {
        String text = vedec.hovor(4821);

        assertTrue(text.contains("Ďakabohu"));
        assertTrue(text.contains("skrutkovačom"));
        assertTrue(text.contains("vzorkou"));
    }

    @Test
    void prvyRozhovorNastaviDialogDokonceny() {
        vedec.hovor(4821);

        assertTrue(vedec.isDialogDokonceny());
    }

    @Test
    void druhyRozhovorJeStrucnePripomenutieSKodom() {
        vedec.hovor(4821);

        String text = vedec.hovor(4821);

        assertTrue(text.startsWith("Ak si zabudol kód"));
        assertTrue(text.contains("4821"));
        assertFalse(text.contains("Ďakabohu"));
    }

    @Test
    void druhyRozhovorJeKratsiAkoPrvy() {
        String prvy = vedec.hovor(4821);
        String druhy = vedec.hovor(4821);

        assertTrue(druhy.length() < prvy.length());
    }

    @Test
    void dalsieRozhovoryOstavajuStrucne() {
        vedec.hovor(4821);
        String druhy = vedec.hovor(4821);

        assertEquals(druhy, vedec.hovor(4821));
        assertTrue(vedec.isDialogDokonceny());
    }

    @Test
    void kodZamkuSaBereZParametraPriKazdomVolani() {
        vedec.hovor(1111);

        String text = vedec.hovor(9999);

        assertTrue(text.contains("9999"));
        assertFalse(text.contains("1111"));
    }

    @Test
    void nastavenieDialoguNaDokonceneSkratiRozhovor() {
        vedec.setDialogDokonceny(true);

        String text = vedec.hovor(4821);

        assertTrue(text.startsWith("Ak si zabudol kód"));
    }

    @Test
    void zrusenieDokonceniaDialoguZnovuSpustiUvodnyRozhovor() {
        vedec.hovor(4821);
        vedec.setDialogDokonceny(false);

        String text = vedec.hovor(4821);

        assertTrue(text.contains("Ďakabohu"));
        assertTrue(vedec.isDialogDokonceny());
    }
}