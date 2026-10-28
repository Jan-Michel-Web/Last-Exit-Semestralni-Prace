package logika.prikazy;

import logika.HerniKontext;

public class MapaPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "mapa";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        kontext.vypisMapu();
    }
}
