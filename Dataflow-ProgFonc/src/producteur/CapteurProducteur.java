package producteur;

import source.Capteur;
import utils.Producteur;

import java.util.Random;

public class CapteurProducteur {
    private static final String[] TYPES = {"Température", "Humidité", "Pression", "CO2"};
    private static final String[] LOCALISATIONS = {"Bâtiment A", "Bâtiment B", "Serre Extérieure"};
    private static final Random RANDOM = new Random();

    public static Producteur<Capteur> creerProducteur() {
        return new Producteur<>(() -> Capteur.generate(
                TYPES[RANDOM.nextInt(TYPES.length)],
                LOCALISATIONS[RANDOM.nextInt(LOCALISATIONS.length)]
        ));
    }
}
