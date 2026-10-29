
package logika.miestnosti;

import logika.HerniKontext;

public class PreskumajRiadiaceCentrum implements StrategiaPreskumania {

    @Override
    public void preskumaj(HerniKontext kontext, String objekt) {
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
}
