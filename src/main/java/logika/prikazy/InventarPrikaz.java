package logika.prikazy;

import logika.HerniKontext;

public class InventarPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "inventar";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        kontext.getHrac().vypisInventar();
    }
}
