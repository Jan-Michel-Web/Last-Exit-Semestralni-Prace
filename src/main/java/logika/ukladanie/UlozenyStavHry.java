package logika.ukladanie;

import java.util.List;

/** Nemenný snímok stavu hry používaný pri ukladaní a načítaní. */
public record UlozenyStavHry(
        int miestnostHraca,
        List<String> predmetyHraca,
        long zostavajuceSekundy,
        int kodLaboratorium,
        int kodZamok,
        int kodFinalny,
        boolean dvereLaboratoriumOtvorene,
        boolean dvereKomoraOtvorene,
        boolean zamokDeaktivovany,
        boolean servisnyPanelOtvoreny,
        boolean chladenieOpravene,
        boolean kartaOverena,
        boolean finalnyKodOvereny,
        boolean poznamkyPrecitane,
        boolean dialogDokonceny,
        List<List<String>> predmetyMiestnosti) {

    public UlozenyStavHry {
        predmetyHraca = List.copyOf(predmetyHraca);
        predmetyMiestnosti = predmetyMiestnosti.stream()
                .map(List::copyOf)
                .toList();
    }
}
