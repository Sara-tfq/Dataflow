package producteur;

import source.Capteur;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CapteurProducteur {
    private static final String[] TYPES = {"Température", "Humidité", "Pression"};
    private static final String[] LOCALISATIONS = {"Paris", "Lyon", "Marseille"};

    public static List<Capteur> genererCapteurs(int nombre) {
        return IntStream.range(0, nombre)
                .mapToObj(i -> Capteur.generate(
                        TYPES[i % TYPES.length],
                        LOCALISATIONS[i % LOCALISATIONS.length]
                ))
                .collect(Collectors.toList());
    }
}

