package logika.miestnosti;

import logika.HernyObjekt;
import logika.Pomocnik;
import logika.predmety.Predmet;

import java.util.ArrayList;
import java.util.List;

public class Miestnost extends HernyObjekt {

    private final String moznosti; // text pre príkaz napoveda
    private final List<Predmet> predmety = new ArrayList<>();
    private final StrategiaPreskumania strategia;

    public Miestnost(
            String nazov,
            String popis,
            String moznosti,
            StrategiaPreskumania strategia) {
        super(nazov, popis);
        this.moznosti = moznosti;
        this.strategia = strategia;
    }

    public StrategiaPreskumania getStrategia() {
        return strategia;
    }

    public String getMoznosti() {
        return moznosti;
    } //Táto metóda umožňuje získať text uložený v atribúte moznosti.

    // Vraciame kópiu zoznamu, aby ho nikto zvonku nemohol meniť (zapuzdrenie).
    public List<Predmet> getPredmety() {
        return new ArrayList<>(predmety);
    }

    public void pridajPredmet(Predmet predmet) {
        predmety.add(predmet);
    }

    public void odoberPredmet(Predmet predmet) {
        predmety.remove(predmet);
    }

    // Stream + lambda: nájde prvý predmet, ktorého názov obsahuje hľadaný text.
    public Predmet najdiPredmet(String text) {
        return predmety.stream()
                .filter(p -> Pomocnik.obsahuje(p.getNazov(), text))
                .findFirst()
                .orElse(null);
    }
}
