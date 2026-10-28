package logika.prikazy;

import logika.HerniKontext;

public class UlozPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "uloz";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        kontext.ulozHru();
    }
}
