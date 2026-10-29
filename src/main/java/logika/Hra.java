package logika;

import logika.logovanie.SpravcaLogov;
import logika.prikazy.*;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Hlavný koordinátor aplikácie: ponuka, načítanie vstupu a smerovanie príkazov.
 * Herný stav je v HerniStav/HerniKontext, jednotlivé príkazy v logika.prikazy
 * a všetka serializácia v UloziskoHry.
 */
public class Hra {
    private static final Set<String> PRIKAZY_BEZ_ARGUMENTU = Set.of(
            "napoveda", "hovorit", "kod", "mapa", "inventar", "cas", "uloz", "koniec"
    );

    private final HerniKontext kontext;
    private final Map<String, HerniPrikaz> prikazy = vytvorPrikazy();

    public Hra() {
        this(new Scanner(System.in));
    }

    Hra(Scanner scanner) {
        kontext = new HerniKontext(scanner);
    }

    private static Map<String, HerniPrikaz> vytvorPrikazy() {
        Map<String, HerniPrikaz> registrovane = new LinkedHashMap<>();
        List<HerniPrikaz> vsetkyPrikazy = List.of(
                new ChodPrikaz(), new ZoberPrikaz(), new HovoritPrikaz(),
                new PreskumajPrikaz(), new PrecitajPrikaz(), new PouziPrikaz(),
                new KodPrikaz(), new NapovedaPrikaz(), new MapaPrikaz(),
                new InventarPrikaz(), new CasPrikaz(), new UlozPrikaz(),
                new KoniecPrikaz()
        );
        for (HerniPrikaz prikaz : vsetkyPrikazy) {
            registrovane.put(prikaz.nazov(), prikaz);
        }
        return Collections.unmodifiableMap(registrovane);
    }

    public void spusti() {
        while (kontext.isProgramBezi()) {
            boolean ulozenaHraExistuje = kontext.maUlozenuHru();

            if (!ulozenaHraExistuje && !kontext.isMenuPoVyprsani()) {
                novaHra();
                kontext.vypisUvod();
                spustiHernyCyklus();
                continue;
            }

            vypisMenu(ulozenaHraExistuje);
            String volba = Pomocnik.normalizuj(kontext.citajVstup()).replaceAll("\\s+", " ");

            if (jeNovaHra(volba)) {
                if (ulozenaHraExistuje && !potvrdNovuHru()) {
                    continue;
                }
                if (ulozenaHraExistuje) {
                    try {
                        kontext.vymazUlozenuHru();
                    } catch (IOException e) {
                        SpravcaLogov.zalogujVarovanie(
                                "Pred novou hrou sa nepodarilo vymazať staré uloženie.", e
                        );
                        continue;
                    }
                }
                kontext.setMenuPoVyprsani(false);
                novaHra();
                kontext.vypisUvod();
                spustiHernyCyklus();
            } else if (jeNacitanie(volba) && ulozenaHraExistuje) {
                if (nacitajHru()) {
                    kontext.setMenuPoVyprsani(false);
                    kontext.vypisUvodPoNacitani();
                    spustiHernyCyklus();
                }
            } else if (kontext.isMenuPoVyprsani() && jeKoniec(volba)) {
                kontext.setProgramBezi(false);
            } else {
                System.out.println("Neplatná voľba. Skús to znova.");
            }
        }
        System.out.println("Program ukončený.");
    }

    private void vypisMenu(boolean ulozenaHraExistuje) {
        System.out.println("\n==============================================");
        System.out.println("                 LAST EXIT");
        System.out.println("==============================================");
        System.out.println("Nová hra");
        if (ulozenaHraExistuje) System.out.println("Načítať uloženú hru");
        if (kontext.isMenuPoVyprsani()) System.out.println("Koniec");
        System.out.print("Vyber možnosť: ");
    }

    private boolean potvrdNovuHru() {
        System.out.println("Existujúca uložená hra bude vymazaná.");
        System.out.print("Naozaj chceš začať novú hru? (ano/nie): ");
        if (Pomocnik.normalizuj(kontext.citajVstup()).equals("ano")) return true;
        System.out.println("Nová hra bola zrušená.");
        return false;
    }

    private boolean jeNovaHra(String volba) {
        return volba.equals("nova hra") || volba.equals("spusti novu hru")
                || volba.equals("zacat novu hru") || volba.equals("zacni novu hru");
    }

    private boolean jeNacitanie(String volba) {
        return volba.equals("nacitat ulozenu hru") || volba.equals("nacitaj ulozenu hru");
    }

    private boolean jeKoniec(String volba) {
        return volba.equals("koniec") || volba.equals("konec");
    }

    private void spustiHernyCyklus() {
        while (kontext.isHraBezi() && kontext.isProgramBezi()) {
            System.out.print("\n[" + kontext.getCasovac().format() + "] > ");
            String riadok = kontext.citajVstup();
            if (kontext.getCasovac().uplynul()) {
                kontext.vybuch();
                continue;
            }
            spracuj(riadok);
        }
    }

    /** Názov metódy ostáva stabilný aj pre existujúce integračné testy. */
    private void spracuj(String riadok) {
        Pomocnik.Prikaz rozdeleny = Pomocnik.rozdelPrikaz(riadok);
        String nazov = rozdeleny.nazov();
        String argument = rozdeleny.argument();
        if (nazov.isEmpty()) return;

        HerniPrikaz prikaz = prikazy.get(nazov);
        if (prikaz == null) {
            if (nazov.matches("\\d{4}")) {
                System.out.println("Kód sa nezadáva priamo. Najprv napíš príkaz 'kod' pri zariadení, a až potom čísla.");
            } else {
                System.out.println("Neznámy príkaz. Napíš 'napoveda'.");
            }
            return;
        }

        if (PRIKAZY_BEZ_ARGUMENTU.contains(nazov) && !argument.isEmpty()) {
            System.out.println("Príkaz '" + nazov + "' neprijíma ďalšie slová. Zadaj iba '" + nazov + "'.");
            return;
        }
        if (nazov.equals("precitaj") && !(argument.isEmpty() || argument.equals("poznamky"))) {
            System.out.println("Použi 'precitaj' alebo 'precitaj poznamky'.");
            return;
        }
        prikaz.vykonaj(kontext, argument);
    }

    // Tenké delegácie zachované kvôli doterajším testom/reflexii.
    private void novaHra() {
        kontext.novaHra();
    }

    private boolean nacitajHru() {
        return kontext.nacitajHru();
    }

    HerniKontext getKontext() {
        return kontext;
    }
}
