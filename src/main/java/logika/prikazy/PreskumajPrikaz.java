
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

        kontext.aktualnaMiestnost()
                .getStrategia()
                .preskumaj(kontext, objekt);
    }
}
