package logika.prikazy;

import logika.HerniKontext;
import logika.predmety.Predmet;

public class PrecitajPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "precitaj";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        if (!kontext.getHrac().ma(Predmet.POZNAMKY)) {
            System.out.println("Nemáš čo čítať. Najprv musíš nájsť a zobrať poznámky.");
            return;
        }
        System.out.println("--- POZNÁMKY ---");
        System.out.println("* Experiment Z-7 drží pod kontrolou iba chladiaci systém v dekontaminačnej komore.");
        System.out.println("* Ak chladenie vypadne, bezpečnostný systém uzamkne celý komplex.");
        System.out.println("* Kód k dverám do riadiaceho centra som si sem zapísal, ale rozlial som naň kávu.");
        System.out.println("  Pamätám si len, že je to štvorciferné číslo, budem ho musieť hádať.");
        System.out.println("* Bezpečnostný zámok sa vypína na termináli v riadiacom centre.");
        System.out.println("  Servisný panel sa otvára skrutkovačom.");
        System.out.println("(Kód na paneli zadáš príkazom 'kod'.)");
        kontext.setPoznamkyPrecitane(true);
        System.out.println("Teraz skús zadať kód príkazom 'kod'.");
    }
}
