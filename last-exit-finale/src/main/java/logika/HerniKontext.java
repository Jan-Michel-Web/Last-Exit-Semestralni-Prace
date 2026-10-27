package logika;

import logika.miestnosti.Miestnost;
import logika.miestnosti.TvorcaMiestnosti;
import logika.predmety.Predmet;
import logika.ukladanie.UlozenyStavHry;
import logika.ukladanie.UloziskoHry;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Konkrétna herná relácia. Príkazy cez ňu čítajú a menia stav hry; nie je to
 * interface s jedinou implementáciou, ale objekt, ktorý spája stav, vstup,
 * časovač a aplikačné služby.
 */
public final class HerniKontext {
    private final Scanner scanner;
    private final HerniStav stav = new HerniStav();
    private final TvorcaMiestnosti tvorcaMiestnosti = new TvorcaMiestnosti();
    private final UloziskoHry ulozisko = new UloziskoHry();

    public HerniKontext(Scanner scanner) {
        this.scanner = scanner;
    }

    public void novaHra() {
        stav.novaHra(tvorcaMiestnosti);
    }

    public boolean maUlozenuHru() {
        return ulozisko.existuje();
    }

    public void vymazUlozenuHru() throws IOException {
        ulozisko.vymaz();
    }

    public boolean nacitajHru() {
        if (!ulozisko.existuje()) {
            System.out.println("Uložená hra sa nenašla. Najprv ulož hru príkazom 'uloz'.");
            return false;
        }
        try {
            UlozenyStavHry ulozenyStav = ulozisko.nacitaj();
            stav.obnovZoUlozenia(ulozenyStav, tvorcaMiestnosti);
            return true;
        } catch (IOException e) {
            System.out.println("Uloženú hru sa nepodarilo načítať: " + e.getMessage());
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Uložený súbor má neplatný obsah. Hru sa nepodarilo načítať.");
        }
        return false;
    }

    /** Ukladanie dát vykonáva UloziskoHry; tu je iba interakcia s hráčom. */
    public void ulozHru() {
        getCasovac().pozastav();
        try {
            ulozisko.uloz(stav);
            System.out.println("Hra bola uložená do súboru '" + ulozisko.getCesta() + "'.");
            System.out.println("Časovač je pozastavený. Zostávajúci čas: " + getCasovac().format());
            System.out.println("Napíš 'pokracovat', ak chceš pokračovať, alebo 'koniec', ak chceš hru ukončiť.");
            while (true) {
                System.out.print("> ");
                String odpoved = Pomocnik.normalizuj(citajVstup());
                if (odpoved.equals("pokracovat")) {
                    getCasovac().pokracuj();
                    System.out.println("Pokračuješ v hre.");
                    return;
                }
                if (odpoved.equals("koniec")) {
                    setHraBezi(false);
                    setProgramBezi(false);
                    System.out.println("Hra bola uložená. Program ukončený.");
                    return;
                }
                System.out.println("Napíš 'pokracovat' alebo 'koniec'.");
            }
        } catch (IOException e) {
            getCasovac().pokracuj();
            System.out.println("Hru sa nepodarilo uložiť: " + e.getMessage());
        }
    }

    public void vypisUvod() {
        System.out.println("\n==============================================");
        System.out.println("      ÚNIK Z PODZEMNÉHO LABORATÓRIA");
        System.out.println("==============================================");
        System.out.println("Chladiaci systém experimentu zlyhal! Bezpečnostný systém uzamkol komplex.");
        System.out.println("Máš 15 minút, kým experiment kriticky zlyhá.\n");
        System.out.println("ZOBUDIL SI SA V LABORATÓRIU. PRESKÚMAJ MOŽNOSTI.\n");
        vypisMapu();
        System.out.println();
        vypisNapovedu();
    }

    public void vypisUvodPoNacitani() {
        System.out.println("\n==============================================");
        System.out.println("      ÚNIK Z PODZEMNÉHO LABORATÓRIA");
        System.out.println("==============================================");
        System.out.println("Hra bola úspešne načítaná.");
        System.out.println("Pokračuješ tam, kde si skončil.");
        System.out.println("Zostávajúci čas: " + getCasovac().format() + "\n");
        vypisMapu();
        System.out.println();
        vypisMiestnost();
        System.out.println();
        vypisNapovedu();
    }

    public void vypisNapovedu() {
        System.out.println("DOSTUPNÉ PRÍKAZY:");
        System.out.println(" chod <miestnosť>        - presun do inej miestnosti");
        System.out.println(" zober <predmet>         - zoberie predmet z miestnosti");
        System.out.println(" hovorit                 - rozhovor s vedcom");
        System.out.println(" preskumaj [vec]         - preskúma miestnosť alebo vec");
        System.out.println(" precitaj                - prečíta poznámky");
        System.out.println(" pouzi <predmet> na <vec> - použije predmet");
        System.out.println(" kod                     - zadá kód na zariadení v miestnosti");
        System.out.println(" mapa                    - ukáže miestnosti a tvoju polohu");
        System.out.println(" inventar, cas, uloz, koniec, napoveda");
        System.out.println("\nV TEJTO MIESTNOSTI SKÚS: " + aktualnaMiestnost().getMoznosti());
    }

    public void vypisMapu() {
        StringBuilder sb = new StringBuilder("MAPA: ");
        for (int i = 0; i < getMiestnosti().size(); i++) {
            if (i > 0) sb.append(" - ");
            sb.append(getMiestnosti().get(i).getNazov());
            if (i == getHrac().getMiestnost()) sb.append(" (aktuálna poloha)");
        }
        System.out.println(sb);
    }

    public void vypisMiestnost() {
        Miestnost miestnost = aktualnaMiestnost();
        System.out.println("\n=== " + miestnost.getNazov().toUpperCase() + " ===");
        System.out.println(miestnost.getPopis());
        vypisPredmety();
    }

    public void vypisPredmety() {
        List<Predmet> predmety = aktualnaMiestnost().getPredmety();
        if (predmety.isEmpty()) {
            System.out.println("Nevidíš tu žiadne predmety, ktoré by sa dali zobrať.");
        } else {
            System.out.println("Predmety, ktoré môžeš zobrať:");
            predmety.forEach(p -> System.out.println(" - " + p.getNazov()));
        }
    }

    public void skontrolujUnik() {
        List<String> chyba = new java.util.ArrayList<>();
        if (!isChladenieOpravene()) chyba.add("opraviť chladiaci systém");
        if (!isKartaOverena()) chyba.add("overiť prístupovú kartu");
        if (!isFinalnyKodOvereny()) chyba.add("zadať finálny kód");

        if (!chyba.isEmpty()) {
            System.out.println("Núdzový východ sa ešte neotvoril. Zostáva: " + String.join(", ", chyba) + ".");
            return;
        }

        System.out.println("\n*** NÚDZOVÝ VÝCHOD SA OTVÁRA ***");
        System.out.println("Vybehol si von tesne predtým, než experiment zlyhal. VYHRAL SI!");
        System.out.println("Zostávajúci čas: " + getCasovac().format());
        try {
            ulozisko.vymaz();
        } catch (IOException e) {
            System.out.println("Upozornenie: Starú uloženú hru sa nepodarilo vymazať: " + e.getMessage());
        }
        ponukniRestart();
    }

    public void vybuch() {
        System.out.println("\n*** BUM! ***");
        System.out.println("Čas vypršal. Experiment kriticky zlyhal a celý komplex vybuchol.");
        System.out.println("Po skončení časového limitu sa zobrazí hlavná ponuka.");
        stav.setHraBezi(false);
        stav.setMenuPoVyprsani(true);
    }

    private void ponukniRestart() {
        System.out.println("\nChceš to skúsiť znova? Napíš 'pokracovat' pre novú hru alebo 'koniec' pre ukončenie.");
        while (true) {
            System.out.print("> ");
            String odpoved = Pomocnik.normalizuj(citajVstup());
            if (odpoved.equals("pokracovat")) {
                novaHra();
                vypisUvod();
                return;
            }
            if (odpoved.equals("koniec")) {
                System.out.println("Hra ukončená.");
                setHraBezi(false);
                setProgramBezi(false);
                return;
            }
            System.out.println("Napíš 'pokracovat' alebo 'koniec'.");
        }
    }

    public String citajVstup() { return scanner.nextLine().trim(); }

    public Hrac getHrac() { return stav.getHrac(); }
    public Vedec getVedec() { return stav.getVedec(); }
    public List<Miestnost> getMiestnosti() { return stav.getMiestnosti(); }
    public Miestnost aktualnaMiestnost() { return stav.aktualnaMiestnost(); }
    public Casovac getCasovac() { return stav.getCasovac(); }
    public HerniStav getStav() { return stav; }

    public int getKodLaboratorium() { return stav.getKodLaboratorium(); }
    public int getKodZamok() { return stav.getKodZamok(); }
    public int getKodFinalny() { return stav.getKodFinalny(); }
    public boolean dvereSuOdomknute(int index) { return stav.dvereSuOdomknute(index); }
    public boolean[] getDvereOdomknuteKopia() { return stav.getDvereOdomknuteKopia(); }
    public void nastavDvereOdomknute(int index, boolean hodnota) { stav.nastavDvereOdomknute(index, hodnota); }

    public boolean isZamokDeaktivovany() { return stav.isZamokDeaktivovany(); }
    public void setZamokDeaktivovany(boolean hodnota) { stav.setZamokDeaktivovany(hodnota); }
    public boolean isServisnyPanelOtvoreny() { return stav.isServisnyPanelOtvoreny(); }
    public void setServisnyPanelOtvoreny(boolean hodnota) { stav.setServisnyPanelOtvoreny(hodnota); }
    public boolean isChladenieOpravene() { return stav.isChladenieOpravene(); }
    public void setChladenieOpravene(boolean hodnota) { stav.setChladenieOpravene(hodnota); }
    public boolean isKartaOverena() { return stav.isKartaOverena(); }
    public void setKartaOverena(boolean hodnota) { stav.setKartaOverena(hodnota); }
    public boolean isFinalnyKodOvereny() { return stav.isFinalnyKodOvereny(); }
    public void setFinalnyKodOvereny(boolean hodnota) { stav.setFinalnyKodOvereny(hodnota); }
    public boolean isPoznamkyPrecitane() { return stav.isPoznamkyPrecitane(); }
    public void setPoznamkyPrecitane(boolean hodnota) { stav.setPoznamkyPrecitane(hodnota); }
    public boolean isHraBezi() { return stav.isHraBezi(); }
    public void setHraBezi(boolean hodnota) { stav.setHraBezi(hodnota); }
    public boolean isProgramBezi() { return stav.isProgramBezi(); }
    public void setProgramBezi(boolean hodnota) { stav.setProgramBezi(hodnota); }
    public boolean isMenuPoVyprsani() { return stav.isMenuPoVyprsani(); }
    public void setMenuPoVyprsani(boolean hodnota) { stav.setMenuPoVyprsani(hodnota); }
}
