package logika.logovanie;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.*;

public final class SpravcaLogov {

    private static final Logger LOGGER =
            Logger.getLogger(SpravcaLogov.class.getName());

    static {
        try {
            // Vytvorenie adresára pre logovacie súbory
            Path adresar = Path.of("logs");
            Files.createDirectories(adresar);

            // Otvorenie logovacieho súboru
            // true znamená, že sa staršie záznamy neprepíšu
            FileHandler handler = new FileHandler(
                    adresar.resolve("last-exit.log").toString(),
                    true
            );

            // Nastavenie formátu logovacích záznamov
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);

            // Pripojenie handlera k loggeru
            LOGGER.addHandler(handler);
            LOGGER.setLevel(Level.ALL);

            // Zabránenie duplicitnému vypisovaniu do konzoly
            LOGGER.setUseParentHandlers(true);

        } catch (IOException e) {
            System.err.println(
                    "Nepodarilo sa inicializovať logovanie: "
                            + e.getMessage()
            );
        }
    }

    // Zaznamenanie závažnej chyby vrátane detailov výnimky
    public static void zalogujChybu(
            String sprava, Throwable chyba) {

        LOGGER.log(Level.SEVERE, sprava, chyba);
    }

    // Zaznamenanie varovania vrátane detailov výnimky
    public static void zalogujVarovanie(
            String sprava, Throwable chyba) {

        LOGGER.log(Level.WARNING, sprava, chyba);
    }
}

