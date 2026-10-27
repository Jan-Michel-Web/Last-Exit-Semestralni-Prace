package logika.prikazy;

import logika.HerniKontext;
import logika.predmety.Predmet;

public class PreskumajPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "preskumaj";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String objekt) {
        if (objekt.isEmpty()) {
            kontext.vypisMiestnost();
            return;
        }

        Predmet predmet = kontext.aktualnaMiestnost().najdiPredmet(objekt);
        if (predmet == null) {
            predmet = kontext.getHrac().najdiPredmet(objekt);
        }
        if (predmet != null) {
            System.out.println(predmet.getPopis());
            return;
        }

        switch (kontext.getHrac().getMiestnost()) {
            case 0 -> preskumajLaboratorium(kontext, objekt);
            case 1 -> preskumajRiadiaceCentrum(kontext, objekt);
            default -> preskumajKomoru(kontext, objekt);
        }
    }

    private void preskumajLaboratorium(HerniKontext kontext, String objekt) {
        if (objekt.contains("stol") || objekt.contains("vybav")) {
            System.out.println("Pracovný stôl je zasypaný papiermi, skúmavkami a rozbitými prístrojmi.");
            kontext.vypisPredmety();
        } else if (objekt.contains("panel") || objekt.contains("zamok")) {
            if (kontext.dvereSuOdomknute(0)) {
                System.out.println("Panel svieti zeleno. Dvere do riadiaceho centra sú odomknuté.");
            } else {
                System.out.println("Panel s číselným zámkom svieti červeno. Vyžaduje štvorciferný kód (príkaz 'kod').");
            }
        } else {
            System.out.println("Nič zaujímavé si nenašiel.");
        }
    }

    private void preskumajRiadiaceCentrum(HerniKontext kontext, String objekt) {
        if (objekt.contains("terminal") || objekt.contains("pocitac")) {
            if (kontext.isZamokDeaktivovany()) {
                System.out.println("Terminál hlási: BEZPEČNOSTNÝ ZÁMOK DEAKTIVOVANÝ.");
                System.out.println("Záložný kód núdzového východu: " + kontext.getKodFinalny());
            } else {
                System.out.println("Terminál žiada štvorciferný kód na deaktiváciu bezpečnostného zámku (príkaz 'kod').");
            }
        } else if (objekt.contains("servis")) {
            if (kontext.isServisnyPanelOtvoreny()) {
                System.out.println("Servisný panel je otvorený a prázdny.");
            } else if (!kontext.isZamokDeaktivovany()) {
                System.out.println("Servisný panel je blokovaný bezpečnostným zámkom. Najprv ho deaktivuj na termináli.");
            } else {
                System.out.println("Servisný panel je pripevnený skrutkami. Potreboval by si vhodný nástroj.");
            }
        } else if (objekt.contains("bezpec")) {
            System.out.println("Bezpečnostný panel bliká: KOMPLEX UZAMKNUTÝ. Do výbuchu zostáva "
                    + kontext.getCasovac().format() + ".");
            if (kontext.isZamokDeaktivovany()) {
                System.out.println("Stav zámku: DEAKTIVOVANÝ.");
            }
        } else if (objekt.contains("panel")) {
            System.out.println("Ktorý panel myslíš? Napíš: preskumaj servisny panel, alebo: preskumaj bezpecnostny panel");
        } else if (objekt.contains("ved") || objekt.contains("novak") || objekt.contains("elias")) {
            System.out.println("Dr. Elias Novak vyzerá vystrašene, ale sústredene. Napíš 'hovorit', ak chceš s ním hovoriť.");
        } else {
            System.out.println("Nič zaujímavé si nenašiel.");
        }
    }

    private void preskumajKomoru(HerniKontext kontext, String objekt) {
        if (objekt.contains("chlad")) {
            if (kontext.isChladenieOpravene()) {
                System.out.println("Chladiaci systém beží. Teplota experimentu klesá.");
            } else {
                System.out.println("Chladiaci systém je poškodený, z potrubia syčí para. Na opravu treba chemickú vzorku.");
            }
        } else if (objekt.contains("panel")) {
            System.out.println("Bezpečnostný panel núdzového východu má čítačku kariet a klávesnicu.");
            System.out.println(" " + (kontext.isChladenieOpravene() ? "[x]" : "[ ]") + " Chladiaci systém opravený");
            System.out.println(" " + (kontext.isKartaOverena() ? "[x]" : "[ ]") + " Prístupová karta overená");
            System.out.println(" " + (kontext.isFinalnyKodOvereny() ? "[x]" : "[ ]") + " Finálny kód zadaný");
        } else if (objekt.contains("dvere") || objekt.contains("vychod")) {
            System.out.println("Masívne výstupné dvere sú zamknuté. Otvoria sa po oprave chladenia,");
            System.out.println("overení karty a zadaní finálneho kódu.");
        } else {
            System.out.println("Nič zaujímavé si nenašiel.");
        }
    }
}
