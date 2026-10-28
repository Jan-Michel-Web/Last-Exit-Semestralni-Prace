package logika.prikazy;

import logika.HerniKontext;
import logika.miestnosti.Miestnost;
import logika.Pomocnik;

import java.util.ArrayList;
import java.util.List;

public class ChodPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "chod";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String ciel) {
        ciel = Pomocnik.normalizuj(ciel).trim().replaceAll("\\s+", " ");
        if (ciel.startsWith("do ")) {
            ciel = ciel.substring(3).trim();
        }

        ciel = switch (ciel) {
            case "laboratoria" -> "laboratorium";
            case "riadiaceho centra" -> "riadiace centrum";
            case "dekontaminacnej komory" -> "dekontaminacna komora";
            default -> ciel;
        };

        if (ciel.isEmpty()) {
            System.out.println("Kam chceš ísť? Napíš napríklad: chod riadiace centrum");
            return;
        }
        if (ciel.length() < 3) {
            System.out.println("Napíš aspoň 3 písmená názvu miestnosti, napríklad: chod riadiace centrum");
            return;
        }

        List<Miestnost> miestnosti = kontext.getMiestnosti();
        List<Integer> zhody = new ArrayList<>();
        for (int i = 0; i < miestnosti.size(); i++) {
            if (Pomocnik.zacinaSlovami(miestnosti.get(i).getNazov(), ciel)) {
                zhody.add(i);
            }
        }

        if (zhody.isEmpty()) {
            System.out.println("Taká miestnosť neexistuje. Zoznam miestností ukáže príkaz 'mapa'.");
            return;
        }
        if (zhody.size() > 1) {
            System.out.println("Nie je jasné, ktorú miestnosť myslíš. Napíš jej názov presnejšie (pozri 'mapa').");
            return;
        }

        int cielIndex = zhody.get(0);
        int aktualnaIndex = kontext.getHrac().getMiestnost();
        if (cielIndex == aktualnaIndex) {
            System.out.println("V tejto miestnosti už si.");
        } else if (Math.abs(cielIndex - aktualnaIndex) > 1) {
            System.out.println("Tam sa nedá ísť priamo. Najprv musíš prejsť cez susednú miestnosť.");
        } else {
            int dvere = Math.min(cielIndex, aktualnaIndex);
            if (!kontext.dvereSuOdomknute(dvere)) {
                if (dvere == 0) {
                    System.out.println("Dvere sú zamknuté. Na stene je panel s číselným zámkom (príkaz 'kod').");
                } else {
                    System.out.println("Dvere sú zamknuté bezpečnostným zámkom. Deaktivuj ho na termináli.");
                }
            } else {
                kontext.getHrac().setMiestnost(cielIndex);
                System.out.println("Prechádzaš do miestnosti: " + kontext.aktualnaMiestnost().getNazov());
                kontext.vypisMiestnost();
                System.out.println("\nSkús: " + kontext.aktualnaMiestnost().getMoznosti());
                System.out.println("(Všetky príkazy ukáže 'napoveda'.)");
            }
        }
    }
}
