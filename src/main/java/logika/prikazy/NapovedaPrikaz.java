package logika.prikazy;

import logika.HerniKontext;

public class NapovedaPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "napoveda";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        kontext.vypisNapovedu();
    }
}
