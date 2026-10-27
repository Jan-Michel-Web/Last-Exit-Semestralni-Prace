
package logika.miestnosti;

import logika.predmety.Predmet;
import logika.predmety.TvorcaPredmetov;

import java.util.ArrayList;
import java.util.List;

public class TvorcaMiestnosti {

    public List<Miestnost> vytvorMiestnosti() {
        List<Miestnost> miestnosti = new ArrayList<>();

        Miestnost laboratorium = new Miestnost(
                "Experimentálne laboratórium",
                "Stojíš v zdemolovanom laboratóriu. Červené núdzové svetlá blikajú a sirény kvília.\n"
                        + "Pracovný stôl je plný vybavenia. Na stene je panel s číselným zámkom,\n"
                        + "ktorý blokuje dvere do riadiaceho centra.",
                "preskumaj stol, preskumaj panel, zober <predmet>, precitaj poznamky, kod, chod riadiace centrum"
        );

        laboratorium.pridajPredmet(TvorcaPredmetov.vytvor(Predmet.POZNAMKY));
        laboratorium.pridajPredmet(TvorcaPredmetov.vytvor(Predmet.SKRUTKOVAC));

        Miestnost riadiaceCentrum = new Miestnost(
                "Riadiace centrum",
                "Monitory blikajú varovnými správami. Je tu počítačový terminál, bezpečnostný panel\n"
                        + "a servisný panel. Pri konzole stojí vedec Dr. Elias Novak.",
                "hovorit, preskumaj terminal / servisny panel / bezpecnostny panel / vedca, kod,\n"
                        + "pouzi skrutkovac na servisny panel, zober <predmet>, chod laboratorium / dekontaminacna komora"
        );

        Miestnost komora = new Miestnost(
                "Dekontaminačná komora",
                "Vzduch je cítiť chemikáliami. Je tu poškodený núdzový chladiaci systém,\n"
                        + "bezpečnostný panel a zamknuté výstupné dvere.",
                "preskumaj chladiaci system / panel / dvere, pouzi vzorku na chladiaci system,\n"
                        + "pouzi kartu na panel, kod, chod riadiace centrum"
        );

        miestnosti.add(laboratorium);
        miestnosti.add(riadiaceCentrum);
        miestnosti.add(komora);

        return miestnosti;
    }
}
