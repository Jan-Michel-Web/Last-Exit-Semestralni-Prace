
package logika.miestnosti;

import logika.HerniKontext;

public class PreskumajLaboratorium implements StrategiaPreskumania {

    @Override
    public void preskumaj(HerniKontext kontext, String objekt) {
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
}
