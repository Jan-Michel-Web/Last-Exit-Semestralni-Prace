package logika.prikazy;

import logika.HerniKontext;
import logika.predmety.Predmet;

public class ZoberPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "zober";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String text) {
        if (text.isEmpty()) {
            System.out.println("Čo chceš zobrať? Napríklad: zober skrutkovac");
            return;
        }

        Predmet predmet = kontext.aktualnaMiestnost().najdiPredmet(text);
        if (predmet == null) {
            Predmet uMna = kontext.getHrac().najdiPredmet(text);
            if (uMna != null) {
                System.out.println("Predmet '" + uMna.getNazov() + "' už máš v inventári.");
            } else {
                System.out.println("Taký predmet tu nie je.");
            }
            return;
        }

        kontext.aktualnaMiestnost().odoberPredmet(predmet);
        kontext.getHrac().pridajPredmet(predmet);
        System.out.println("Zobral si: " + predmet.getNazov());
        if (predmet.getNazov().equals(Predmet.POZNAMKY)) {
            System.out.println("(Môžeš si ich prečítať príkazom 'precitaj'.)");
        }
    }
}
