package logika.prikazy;

import logika.HerniKontext;
//hlavičky metod ktoré používajú ostatné triedy
/**
 * Spoločné rozhranie pre všetky používateľské príkazy hry.
 * Každý príkaz pozná svoj názov a vie vykonať svoju operáciu.
 */
public interface HerniPrikaz {

    String nazov();

    void vykonaj(HerniKontext kontext, String argument);
}
