package logika.prikazy;

import logika.HerniKontext;
import logika.predmety.Predmet;
import logika.predmety.TvorcaPredmetov;
import logika.Pomocnik;

import java.util.Arrays;

public class PouziPrikaz implements HerniPrikaz {
    @Override
    public String nazov() {
        return "pouzi";
    }

    @Override
    public void vykonaj(HerniKontext kontext, String argument) {
        if (argument.isEmpty()) {
            System.out.println("Čo chceš použiť? Napríklad: pouzi skrutkovac na servisny panel");
            return;
        }

        Predmet predmet = null;
        String objekt = "";
        if (argument.contains(" na ")) {
            String[] casti = argument.split(" na ", 2);
            predmet = kontext.getHrac().najdiPredmet(casti[0].trim());
            objekt = casti[1].trim();
        } else {
            String[] slova = argument.split(" ");
            for (int pocet = slova.length; pocet >= 1 && predmet == null; pocet--) {
                String text = String.join(" ", Arrays.copyOfRange(slova, 0, pocet));
                predmet = kontext.getHrac().najdiPredmet(text);
                if (predmet != null) {
                    objekt = String.join(" ", Arrays.copyOfRange(slova, pocet, slova.length));
                }
            }
        }

        if (predmet == null) {
            System.out.println("Taký predmet nemáš v inventári.");
            return;
        }
        if (objekt.isEmpty()) {
            System.out.println("Na čo ho chceš použiť? Napríklad: pouzi "
                    + Pomocnik.normalizuj(predmet.getNazov()) + " na <vec>");
            return;
        }

        int izba = kontext.getHrac().getMiestnost();
        switch (predmet.getNazov()) {
            case Predmet.SKRUTKOVAC -> pouziSkrutkovac(kontext, objekt, izba);
            case Predmet.KARTA -> pouziKartu(kontext, objekt, izba);
            case Predmet.VZORKA -> pouziVzorku(kontext, objekt, izba);
            default -> System.out.println("Tento predmet sa takto použiť nedá. Poznámky si prečítaš príkazom 'precitaj'.");
        }
    }

    private void pouziSkrutkovac(HerniKontext kontext, String objekt, int izba) {
        if (izba != 1) {
            System.out.println("V tejto miestnosti sa skrutkovač nehodí. Skús ho použiť v inej miestnosti.");
        } else if (objekt.equals("panel")) {
            System.out.println("Ktorý panel myslíš? Napíš napríklad: pouzi skrutkovac na servisny panel");
        } else if (!objekt.contains("servis")) {
            System.out.println("Skrutkovač sa na toto nehodí. Skús ho použiť na servisný panel.");
        } else if (kontext.isServisnyPanelOtvoreny()) {
            System.out.println("Servisný panel je už otvorený.");
        } else if (!kontext.isZamokDeaktivovany()) {
            System.out.println("Panel je stále blokovaný bezpečnostným zámkom. Najprv ho deaktivuj na termináli.");
        } else {
            kontext.setServisnyPanelOtvoreny(true);
            kontext.getMiestnosti().get(1).pridajPredmet(TvorcaPredmetov.vytvor(Predmet.KARTA));
            kontext.getMiestnosti().get(1).pridajPredmet(TvorcaPredmetov.vytvor(Predmet.VZORKA));
            System.out.println("Odskrutkoval si servisný panel. Vo vnútri leží prístupová karta a chemická vzorka!");
            kontext.vypisPredmety();
        }
    }

    private void pouziKartu(HerniKontext kontext, String objekt, int izba) {
        if (izba != 2) {
            System.out.println("V tejto miestnosti sa karta nehodí. Skús ju použiť v inej miestnosti.");
        } else if (!(objekt.contains("panel") || objekt.contains("citack"))) {
            System.out.println("Karta sa na toto nehodí. Skús ju použiť na bezpečnostný panel.");
        } else if (kontext.isKartaOverena()) {
            System.out.println("Karta je už overená.");
        } else {
            kontext.setKartaOverena(true);
            System.out.println("Čítačka pípla: PRÍSTUP OVERENÝ.");
            kontext.skontrolujUnik();
        }
    }

    private void pouziVzorku(HerniKontext kontext, String objekt, int izba) {
        if (izba != 2) {
            System.out.println("V tejto miestnosti sa vzorka nehodí. Skús ju použiť v inej miestnosti.");
        } else if (!objekt.contains("chlad")) {
            System.out.println("Vzorka sa na toto nehodí. Skús ju použiť na chladiaci systém.");
        } else {
            kontext.setChladenieOpravene(true);
            Predmet vzorka = kontext.getHrac().najdiPredmet(Predmet.VZORKA);
            if (vzorka != null) {
                kontext.getHrac().odoberPredmet(vzorka);
            }
            System.out.println("Vyliala si vzorku do chladiaceho okruhu. Systém sa rozbehol, chladenie je OPRAVENÉ.");
            System.out.println("(Chemická vzorka sa spotrebovala.)");
            kontext.skontrolujUnik();
        }
    }
}
