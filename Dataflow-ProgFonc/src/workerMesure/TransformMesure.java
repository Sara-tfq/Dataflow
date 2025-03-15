package workerMesure;

import source.Mesure;
import utils.Transformateur;

public class TransformMesure {
    private static final Transformateur<Mesure> transformateur = new Transformateur<>();

    public static Mesure celsiusToFahrenheit(Mesure mesure) {
        return transformateur.transformer(mesure, m -> new Mesure(m.capteurId(), m.valeur() * 9.0 / 5.0 + 32, m.timestamp()));
    }

    public static Mesure pascalToBar(Mesure mesure) {
        return transformateur.transformer(mesure, m -> new Mesure(m.capteurId(), m.valeur() / 100000, m.timestamp()));
    }
}
