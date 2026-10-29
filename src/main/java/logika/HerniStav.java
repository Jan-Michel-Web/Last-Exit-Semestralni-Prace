package logika;

import logika.miestnosti.Miestnost;
import logika.miestnosti.TvorcaMiestnosti;
import logika.predmety.TvorcaPredmetov;
import logika.ukladanie.UlozenyStavHry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Uchováva meniteľný stav jednej hry. Nečíta vstup, nevypisuje texty a nepracuje
 * priamo so súborom; tieto úlohy majú na starosti iné triedy.
 */
public final class HerniStav {

    private final Casovac casovac = new Casovac();
    private final Random random = new Random();

    private Hrac hrac;
    private Vedec vedec;
    private List<Miestnost> miestnosti = new ArrayList<>();
    private boolean[] dvereOdomknute = new boolean[HerniKonstanty.POCET_MIESTNOSTI - 1];

    private int kodLaboratorium;
    private int kodZamok;
    private int kodFinalny;
    private boolean zamokDeaktivovany;
    private boolean servisnyPanelOtvoreny;
    private boolean chladenieOpravene;
    private boolean kartaOverena;
    private boolean finalnyKodOvereny;
    private boolean poznamkyPrecitane;
    private boolean hraBezi;
    private boolean programBezi = true;
    private boolean menuPoVyprsani;

    public void novaHra(TvorcaMiestnosti tvorcaMiestnosti) {
        hrac = new Hrac();
        vedec = new Vedec("Dr. Elias Novak");
        miestnosti = new ArrayList<>(tvorcaMiestnosti.vytvorMiestnosti());
        dvereOdomknute = new boolean[HerniKonstanty.POCET_MIESTNOSTI - 1];

        zamokDeaktivovany = false;
        servisnyPanelOtvoreny = false;
        chladenieOpravene = false;
        kartaOverena = false;
        finalnyKodOvereny = false;
        poznamkyPrecitane = false;
        hraBezi = true;
        menuPoVyprsani = false;

        kodLaboratorium = nahodnyKod();
        do {
            kodZamok = nahodnyKod();
        } while (kodZamok == kodLaboratorium);
        do {
            kodFinalny = nahodnyKod();
        } while (kodFinalny == kodLaboratorium || kodFinalny == kodZamok);

        casovac.restart();
    }

    public void obnovZoUlozenia(UlozenyStavHry ulozeny, TvorcaMiestnosti tvorcaMiestnosti) {
        novaHra(tvorcaMiestnosti);
        hrac.setMiestnost(ulozeny.miestnostHraca());
        kodLaboratorium = ulozeny.kodLaboratorium();
        kodZamok = ulozeny.kodZamok();
        kodFinalny = ulozeny.kodFinalny();
        dvereOdomknute[0] = ulozeny.dvereLaboratoriumOtvorene();
        dvereOdomknute[1] = ulozeny.dvereKomoraOtvorene();
        zamokDeaktivovany = ulozeny.zamokDeaktivovany();
        servisnyPanelOtvoreny = ulozeny.servisnyPanelOtvoreny();
        chladenieOpravene = ulozeny.chladenieOpravene();
        kartaOverena = ulozeny.kartaOverena();
        finalnyKodOvereny = ulozeny.finalnyKodOvereny();
        poznamkyPrecitane = ulozeny.poznamkyPrecitane();
        vedec.setDialogDokonceny(ulozeny.dialogDokonceny());

        hrac.getPredmety().forEach(hrac::odoberPredmet);
        miestnosti.forEach(miestnost -> miestnost.getPredmety().forEach(miestnost::odoberPredmet));
        ulozeny.predmetyHraca().forEach(nazov -> hrac.pridajPredmet(TvorcaPredmetov.vytvor(nazov)));
        for (int i = 0; i < miestnosti.size(); i++) {
            for (String nazov : ulozeny.predmetyMiestnosti().get(i)) {
                miestnosti.get(i).pridajPredmet(TvorcaPredmetov.vytvor(nazov));
            }
        }
        casovac.nastavZostavajuceSekundy(ulozeny.zostavajuceSekundy());
        hraBezi = true;
    }

    private int nahodnyKod() {
        return HerniKonstanty.MIN_KOD + random.nextInt(HerniKonstanty.MAX_KOD - HerniKonstanty.MIN_KOD + 1);
    }

    public Hrac getHrac() { return hrac; }
    public Vedec getVedec() { return vedec; }
    public List<Miestnost> getMiestnosti() { return Collections.unmodifiableList(miestnosti); }
    public Miestnost aktualnaMiestnost() { return miestnosti.get(hrac.getMiestnost()); }
    public Casovac getCasovac() { return casovac; }
    public int getKodLaboratorium() { return kodLaboratorium; }
    public int getKodZamok() { return kodZamok; }
    public int getKodFinalny() { return kodFinalny; }
    public boolean dvereSuOdomknute(int index) { return dvereOdomknute[index]; }
    public boolean[] getDvereOdomknuteKopia() { return dvereOdomknute.clone(); }
    public void nastavDvereOdomknute(int index, boolean hodnota) { dvereOdomknute[index] = hodnota; }
    public boolean isZamokDeaktivovany() { return zamokDeaktivovany; }
    public void setZamokDeaktivovany(boolean hodnota) { zamokDeaktivovany = hodnota; }
    public boolean isServisnyPanelOtvoreny() { return servisnyPanelOtvoreny; }
    public void setServisnyPanelOtvoreny(boolean hodnota) { servisnyPanelOtvoreny = hodnota; }
    public boolean isChladenieOpravene() { return chladenieOpravene; }
    public void setChladenieOpravene(boolean hodnota) { chladenieOpravene = hodnota; }
    public boolean isKartaOverena() { return kartaOverena; }
    public void setKartaOverena(boolean hodnota) { kartaOverena = hodnota; }
    public boolean isFinalnyKodOvereny() { return finalnyKodOvereny; }
    public void setFinalnyKodOvereny(boolean hodnota) { finalnyKodOvereny = hodnota; }
    public boolean isPoznamkyPrecitane() { return poznamkyPrecitane; }
    public void setPoznamkyPrecitane(boolean hodnota) { poznamkyPrecitane = hodnota; }
    public boolean isHraBezi() { return hraBezi; }
    public void setHraBezi(boolean hodnota) { hraBezi = hodnota; }
    public boolean isProgramBezi() { return programBezi; }
    public void setProgramBezi(boolean hodnota) { programBezi = hodnota; }
    public boolean isMenuPoVyprsani() { return menuPoVyprsani; }
    public void setMenuPoVyprsani(boolean hodnota) { menuPoVyprsani = hodnota; }
}
