package logika.prikazy;

import logika.HerniKontext;

public class HovoritPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "hovorit";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        if (kontext.getHrac().getMiestnost() != 1) {
            System.out.println("Tu nie je nikto, s kým by si mohol hovoriť.");
            return;
        }
        System.out.println(kontext.getVedec().getMeno() + ":");
        System.out.println(kontext.getVedec().hovor(kontext.getKodZamok()));
    }
}
