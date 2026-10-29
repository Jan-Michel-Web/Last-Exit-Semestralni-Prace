package logika.ukladanie;

import logika.HerniStav;
import logika.miestnosti.Miestnost;
import logika.predmety.Predmet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RegistrUlozenia {

    // Klíče uloženého souboru na jednom místě
    public static final String KLUC_MIESTNOST_HRACA = "player.room";
    public static final String KLUC_PREDMETY_HRACA = "player.items";
    public static final String KLUC_ZOSTAVAJUCI_CAS =
            "timer.remainingSeconds";

    public static final String KLUC_KOD_LABORATORIUM = "code.laboratory";
    public static final String KLUC_KOD_ZAMOK = "code.security";
    public static final String KLUC_KOD_FINALNY = "code.final";

    public static final String KLUC_ZAMOK_DEAKTIVOVANY =
            "lock.deactivated";
    public static final String KLUC_SERVISNY_PANEL =
            "servicePanel.open";
    public static final String KLUC_CHLADENIE =
            "cooling.repaired";
    public static final String KLUC_KARTA =
            "card.verified";
    public static final String KLUC_FINALNY_KOD =
            "finalCode.verified";
    public static final String KLUC_POZNAMKY =
            "notes.read";
    public static final String KLUC_DIALOG_VEDCA =
            "scientist.dialogFinished";

    public static String klucDvere(int index) {
        return "door." + index + ".open";
    }

    public static String klucPredmetyMiestnosti(int index) {
        return "room." + index + ".items";
    }

    // Registr: klíč -> způsob získání hodnoty z herního stavu
    private final Map<String, Function<HerniStav, String>> registr =
            new LinkedHashMap<>();

    public RegistrUlozenia() {
        registruj(KLUC_MIESTNOST_HRACA,
                stav -> Integer.toString(stav.getHrac().getMiestnost()));

        registruj(KLUC_PREDMETY_HRACA,
                stav -> spojNazvyPredmetov(
                        stav.getHrac().getPredmety()));

        registruj(KLUC_ZOSTAVAJUCI_CAS,
                stav -> Long.toString(
                        stav.getCasovac().zostavaSekund()));

        registruj(KLUC_KOD_LABORATORIUM,
                stav -> Integer.toString(stav.getKodLaboratorium()));

        registruj(KLUC_KOD_ZAMOK,
                stav -> Integer.toString(stav.getKodZamok()));

        registruj(KLUC_KOD_FINALNY,
                stav -> Integer.toString(stav.getKodFinalny()));

        registruj(KLUC_ZAMOK_DEAKTIVOVANY,
                stav -> Boolean.toString(stav.isZamokDeaktivovany()));

        registruj(KLUC_SERVISNY_PANEL,
                stav -> Boolean.toString(stav.isServisnyPanelOtvoreny()));

        registruj(KLUC_CHLADENIE,
                stav -> Boolean.toString(stav.isChladenieOpravene()));

        registruj(KLUC_KARTA,
                stav -> Boolean.toString(stav.isKartaOverena()));

        registruj(KLUC_FINALNY_KOD,
                stav -> Boolean.toString(stav.isFinalnyKodOvereny()));

        registruj(KLUC_POZNAMKY,
                stav -> Boolean.toString(stav.isPoznamkyPrecitane()));

        registruj(KLUC_DIALOG_VEDCA,
                stav -> Boolean.toString(
                        stav.getVedec().isDialogDokonceny()));
    }

    private void registruj(
            String kluc,
            Function<HerniStav, String> ziskajHodnotu) {

        if (registr.putIfAbsent(kluc, ziskajHodnotu) != null) {
            throw new IllegalArgumentException(
                    "Duplicitný kľúč uloženia: " + kluc);
        }
    }

    public Properties vytvorProperties(HerniStav stav) {
        Properties data = new Properties();

        // Zapísanie všetkých zaregistrovaných položiek
        registr.forEach((kluc, funkcia) ->
                data.setProperty(kluc, funkcia.apply(stav)));

        // Zapísanie stavov dverí
        boolean[] dvere = stav.getDvereOdomknuteKopia();

        for (int i = 0; i < dvere.length; i++) {
            data.setProperty(
                    klucDvere(i),
                    Boolean.toString(dvere[i]));
        }

        // Zapísanie predmetov jednotlivých miestností
        List<Miestnost> miestnosti = stav.getMiestnosti();

        for (int i = 0; i < miestnosti.size(); i++) {
            data.setProperty(
                    klucPredmetyMiestnosti(i),
                    spojNazvyPredmetov(
                            miestnosti.get(i).getPredmety()));
        }

        return data;
    }

    private String spojNazvyPredmetov(List<Predmet> predmety) {
        return predmety.stream()
                .map(Predmet::getNazov)
                .collect(Collectors.joining("|"));
    }
}