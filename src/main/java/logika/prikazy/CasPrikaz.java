package logika.prikazy;

import logika.HerniKontext;

public class CasPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "cas";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        System.out.println("Do výbuchu zostáva: " + kontext.getCasovac().format());
    }
}
