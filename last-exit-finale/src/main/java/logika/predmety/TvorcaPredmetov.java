package logika.predmety;

public final class TvorcaPredmetov {

    private TvorcaPredmetov() {
        // Pomocná třída, nevytváří se její instance.
    }

    public static Predmet vytvor(String nazov) {
        return switch (nazov) {
            case Predmet.POZNAMKY -> new Predmet(
                    Predmet.POZNAMKY,
                    "Pomačkané poznámky popísané tvojím rukopisom. Prečítaj si ich príkazom 'precitaj'."
            );

            case Predmet.SKRUTKOVAC -> new Predmet(
                    Predmet.SKRUTKOVAC,
                    "Malý krížový skrutkovač. Hodí sa na povolenie skrutiek."
            );

            case Predmet.KARTA -> new Predmet(
                    Predmet.KARTA,
                    "Biela prístupová karta s čipom a nápisom LEVEL 3."
            );

            case Predmet.VZORKA -> new Predmet(
                    Predmet.VZORKA,
                    "Skúmavka so žiarivo zelenou kvapalinou. Stabilizátor chladiva."
            );

            default -> throw new IllegalArgumentException(
                    "Neznámy predmet: " + nazov
            );
        };
    }
}
