package logika.ukladanie;

import logika.*;
import logika.predmety.TvorcaPredmetov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/** Zodpovedá za čítanie, validáciu a zápis uloženého stavu hry. */
public final class UloziskoHry {
    private final Path cesta;
    private final RegistrUlozenia registrUlozenia = new RegistrUlozenia();

    public UloziskoHry() {
        this(Paths.get("last_exit_save.properties"));
    }

    public UloziskoHry(Path cesta) {
        this.cesta = cesta;
    }

    public Path getCesta() {
        return cesta;
    }

    public boolean existuje() {
        return Files.exists(cesta);
    }

    public void vymaz() throws IOException {
        Files.deleteIfExists(cesta);
    }

    /** Zapisuje všetky dáta hry do existujúceho, stabilného formátu Properties. */
    public void uloz(HerniStav stav) throws IOException {
        Properties data = registrUlozenia.vytvorProperties(stav);

        try (OutputStream vystup = Files.newOutputStream(cesta)) {
            data.store(vystup, "Ulozena hra Last Exit");
        }
    }

    /** Načíta a validuje uloženie; živý herný stav ešte nemení. */
    public UlozenyStavHry nacitaj() throws IOException {
        Properties data = new Properties();

        try (InputStream vstup = Files.newInputStream(cesta)) {
            data.load(vstup);
        }

        int miestnost = nacitajCeleCislo(
                data, RegistrUlozenia.KLUC_MIESTNOST_HRACA
        );

        if (miestnost < 0 || miestnost >= HerniKonstanty.POCET_MIESTNOSTI) {
            throw new IllegalArgumentException(
                    "Neplatná miestnosť v uloženej hre."
            );
        }

        long zostavajuceSekundy = nacitajDlheCislo(
                data, RegistrUlozenia.KLUC_ZOSTAVAJUCI_CAS
        );

        if (zostavajuceSekundy < 0
                || zostavajuceSekundy > Casovac.getLimitSekundy()) {
            throw new IllegalArgumentException(
                    "Neplatný čas v uloženej hre."
            );
        }

        int kodLaboratorium = nacitajKod(
                data, RegistrUlozenia.KLUC_KOD_LABORATORIUM
        );

        int kodZamok = nacitajKod(
                data, RegistrUlozenia.KLUC_KOD_ZAMOK
        );

        int kodFinalny = nacitajKod(
                data, RegistrUlozenia.KLUC_KOD_FINALNY
        );

        if (kodLaboratorium == kodZamok
                || kodLaboratorium == kodFinalny
                || kodZamok == kodFinalny) {
            throw new IllegalArgumentException(
                    "Kódy v uloženej hre sa nesmú opakovať."
            );
        }

        List<String> predmetyHraca = rozdelNazvy(
                nacitajPovinnuHodnotu(
                        data, RegistrUlozenia.KLUC_PREDMETY_HRACA
                )
        );

        List<List<String>> predmetyMiestnosti = new ArrayList<>();

        for (int i = 0; i < HerniKonstanty.POCET_MIESTNOSTI; i++) {
            predmetyMiestnosti.add(
                    rozdelNazvy(
                            data.getProperty(
                                    RegistrUlozenia.klucPredmetyMiestnosti(i),
                                    ""
                            )
                    )
            );
        }

        // Overenie, či všetky názvy predmetov existujú.
        predmetyHraca.forEach(TvorcaPredmetov::vytvor);

        predmetyMiestnosti.forEach(
                zoznam -> zoznam.forEach(TvorcaPredmetov::vytvor)
        );

        return new UlozenyStavHry(
                miestnost,
                predmetyHraca,
                zostavajuceSekundy,
                kodLaboratorium,
                kodZamok,
                kodFinalny,
                nacitajBoolean(data, RegistrUlozenia.klucDvere(0)),
                nacitajBoolean(data, RegistrUlozenia.klucDvere(1)),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_ZAMOK_DEAKTIVOVANY
                ),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_SERVISNY_PANEL
                ),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_CHLADENIE
                ),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_KARTA
                ),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_FINALNY_KOD
                ),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_POZNAMKY
                ),
                nacitajBoolean(
                        data, RegistrUlozenia.KLUC_DIALOG_VEDCA
                ),
                predmetyMiestnosti
        );
    }

    private List<String> rozdelNazvy(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(text.split("\\|", -1)));
    }

    private String nacitajPovinnuHodnotu(Properties data, String kluc) {
        String hodnota = data.getProperty(kluc);
        if (hodnota == null) {
            throw new IllegalArgumentException("Chýba údaj: " + kluc);
        }
        return hodnota;
    }

    private int nacitajCeleCislo(Properties data, String kluc) {
        return Integer.parseInt(nacitajPovinnuHodnotu(data, kluc));
    }

    private long nacitajDlheCislo(Properties data, String kluc) {
        return Long.parseLong(nacitajPovinnuHodnotu(data, kluc));
    }

    private int nacitajKod(Properties data, String kluc) {
        int kod = nacitajCeleCislo(data, kluc);
        if (kod < HerniKonstanty.MIN_KOD || kod > HerniKonstanty.MAX_KOD) {
            throw new IllegalArgumentException("Neplatný kód v uloženej hre.");
        }
        return kod;
    }

    private boolean nacitajBoolean(Properties data, String kluc) {
        String hodnota = nacitajPovinnuHodnotu(data, kluc);
        if (!hodnota.equalsIgnoreCase("true") && !hodnota.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("Neplatná hodnota: " + kluc);
        }
        return Boolean.parseBoolean(hodnota);
    }
}
