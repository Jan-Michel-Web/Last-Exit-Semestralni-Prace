
package logika.miestnosti;

import logika.HerniKontext;

public class PreskumajKomoru implements StrategiaPreskumania {

    @Override
    public void preskumaj(HerniKontext kontext, String objekt) {
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
