package logika.prikazy;

import logika.HerniKontext;

public class KoniecPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "koniec";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        System.out.println("Hra ukončená.");
        kontext.setHraBezi(false);
        kontext.setProgramBezi(false);
    }
}
