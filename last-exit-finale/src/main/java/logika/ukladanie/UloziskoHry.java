package logika.ukladanie;

import logika.*;
import logika.miestnosti.Miestnost;
import logika.predmety.Predmet;
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
import java.util.stream.Collectors;

/** Jediné miesto v aplikácii, ktoré pozná formát a súbor uloženia hry. */
public final class UloziskoHry {
    private final Path cesta;

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
        Properties data = new Properties();
        data.setProperty("player.room", Integer.toString(stav.getHrac().getMiestnost()));
        data.setProperty("player.items", spojNazvyPredmetov(stav.getHrac().getPredmety()));
        data.setProperty("timer.remainingSeconds", Long.toString(stav.getCasovac().zostavaSekund()));
        data.setProperty("code.laboratory", Integer.toString(stav.getKodLaboratorium()));
        data.setProperty("code.security", Integer.toString(stav.getKodZamok()));
        data.setProperty("code.final", Integer.toString(stav.getKodFinalny()));
        data.setProperty("door.0.open", Boolean.toString(stav.dvereSuOdomknute(0)));
        data.setProperty("door.1.open", Boolean.toString(stav.dvereSuOdomknute(1)));
        data.setProperty("lock.deactivated", Boolean.toString(stav.isZamokDeaktivovany()));
        data.setProperty("servicePanel.open", Boolean.toString(stav.isServisnyPanelOtvoreny()));
        data.setProperty("cooling.repaired", Boolean.toString(stav.isChladenieOpravene()));
        data.setProperty("card.verified", Boolean.toString(stav.isKartaOverena()));
        data.setProperty("finalCode.verified", Boolean.toString(stav.isFinalnyKodOvereny()));
        data.setProperty("notes.read", Boolean.toString(stav.isPoznamkyPrecitane()));
        data.setProperty("scientist.dialogFinished", Boolean.toString(stav.getVedec().isDialogDokonceny()));

        List<Miestnost> miestnosti = stav.getMiestnosti();
        for (int i = 0; i < miestnosti.size(); i++) {
            data.setProperty("room." + i + ".items", spojNazvyPredmetov(miestnosti.get(i).getPredmety()));
        }

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

        int miestnost = nacitajCeleCislo(data, "player.room");
        if (miestnost < 0 || miestnost >= 3) {
            throw new IllegalArgumentException("Neplatná miestnosť v uloženej hre.");
        }

        long zostavajuceSekundy = nacitajDlheCislo(data, "timer.remainingSeconds");
        if (zostavajuceSekundy < 0 || zostavajuceSekundy > Casovac.getLimitSekundy()) {
            throw new IllegalArgumentException("Neplatný čas v uloženej hre.");
        }

        int kodLaboratorium = nacitajKod(data, "code.laboratory");
        int kodZamok = nacitajKod(data, "code.security");
        int kodFinalny = nacitajKod(data, "code.final");
        if (kodLaboratorium == kodZamok || kodLaboratorium == kodFinalny || kodZamok == kodFinalny) {
            throw new IllegalArgumentException("Kódy v uloženej hre sa nesmú opakovať.");
        }

        List<String> predmetyHraca = rozdelNazvy(nacitajPovinnuHodnotu(data, "player.items"));
        List<List<String>> predmetyMiestnosti = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            predmetyMiestnosti.add(rozdelNazvy(data.getProperty("room." + i + ".items", "")));
        }

        // Neznámy názov predmetu zneplatní uloženie ešte pred zmenou aktuálnej hry.
        predmetyHraca.forEach(TvorcaPredmetov::vytvor);
        predmetyMiestnosti.forEach(zoznam -> zoznam.forEach(TvorcaPredmetov::vytvor));

        return new UlozenyStavHry(
                miestnost,
                predmetyHraca,
                zostavajuceSekundy,
                kodLaboratorium,
                kodZamok,
                kodFinalny,
                nacitajBoolean(data, "door.0.open"),
                nacitajBoolean(data, "door.1.open"),
                nacitajBoolean(data, "lock.deactivated"),
                nacitajBoolean(data, "servicePanel.open"),
                nacitajBoolean(data, "cooling.repaired"),
                nacitajBoolean(data, "card.verified"),
                nacitajBoolean(data, "finalCode.verified"),
                nacitajBoolean(data, "notes.read"),
                nacitajBoolean(data, "scientist.dialogFinished"),
                predmetyMiestnosti);
    }

    private String spojNazvyPredmetov(List<Predmet> predmety) {
        return predmety.stream().map(Predmet::getNazov).collect(Collectors.joining("|"));
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
        if (kod < 1000 || kod > 9999) {
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
