package logika.prikazy;

import logika.HerniKontext;
import logika.predmety.Predmet;
import logika.Pomocnik;

public class KodPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "kod";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        int izba = kontext.getHrac().getMiestnost();
        if (izba == 0) {
            if (kontext.dvereSuOdomknute(0)) {
                System.out.println("Zámok je už odomknutý.");
            } else if (!kontext.getHrac().ma(Predmet.POZNAMKY)) {
                System.out.println("Nevieš, aký kód zadať. Možno ti pomôžu poznámky na stole.");
            } else if (!kontext.isPoznamkyPrecitane()) {
                System.out.println("Najprv si prečítaj poznámky príkazom 'precitaj'.");
            } else if (zadajKod(kontext, kontext.getKodLaboratorium(), true)) {
                kontext.nastavDvereOdomknute(0, true);
                System.out.println("Zámok cvakol. Dvere do riadiaceho centra sú odomknuté!");
            }
        } else if (izba == 1) {
            if (kontext.isZamokDeaktivovany()) {
                System.out.println("Bezpečnostný zámok je už deaktivovaný.");
            } else if (zadajKod(kontext, kontext.getKodZamok(), false)) {
                kontext.setZamokDeaktivovany(true);
                kontext.nastavDvereOdomknute(1, true);
                System.out.println("BEZPEČNOSTNÝ ZÁMOK DEAKTIVOVANÝ. Dvere do dekontaminačnej komory sa odomkli.");
                System.out.println("Servisný panel je už odblokovaný. Terminál zobrazuje aj záložný kód núdzového východu.");
            }
        } else {
            if (kontext.isFinalnyKodOvereny()) {
                System.out.println("Finálny kód je už zadaný.");
            } else if (zadajKod(kontext, kontext.getKodFinalny(), false)) {
                kontext.setFinalnyKodOvereny(true);
                System.out.println("Panel pípol: FINÁLNY KÓD PRIJATÝ.");
                kontext.skontrolujUnik();
            }
        }
    }

    private boolean zadajKod(HerniKontext kontext, int spravny, boolean napoveda) {
        if (napoveda) {
            System.out.println("Kód nepoznáš presne, musíš ho hádať. Po každom tipe ti panel napíše,");
            System.out.println("či je hľadaný kód MENŠÍ alebo VÄČŠÍ ako tvoj tip.");
            System.out.println("(Kód je štvorciferný, od 1000 do 9999.)");
        }
        System.out.println("Zadaj štvorciferný kód (alebo napíš 'spat' pre zrušenie).");
        while (true) {
            System.out.print("[" + kontext.getCasovac().format() + "] KÓD > ");
            String vstup = kontext.citajVstup();

            if (kontext.getCasovac().uplynul()) {
                kontext.vybuch();
                return false;
            }
            if (Pomocnik.normalizuj(vstup).equals("spat")) {
                System.out.println("Zrušil si zadávanie kódu.");
                return false;
            }
            if (!vstup.matches("[0-9]{4}") || vstup.charAt(0) == '0') {
                System.out.println("Zadaj štvorciferné číslo od 1000 do 9999 (alebo 'spat' pre zrušenie).");
                continue;
            }

            int tip = Integer.parseInt(vstup);
            if (tip == spravny) {
                return true;
            }
            if (napoveda) {
                if (tip > spravny) {
                    System.out.println("MENŠIE (hľadaný kód je menší ako tvoj tip)");
                } else {
                    System.out.println("VÄČŠIE (hľadaný kód je väčší ako tvoj tip)");
                }
            } else {
                System.out.println("Nesprávny kód.");
            }
        }
    }
}
