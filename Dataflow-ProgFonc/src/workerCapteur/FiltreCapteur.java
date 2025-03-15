package workerCapteur;

import source.Capteur;
import utils.Filtre;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FiltreCapteur {
    private static final Filtre<Capteur> filtre = new Filtre<>();

    public static List<Capteur> filtrer(Stream<Capteur> capteurs, Predicate<Capteur> predicate) {
        return filtre.filtrer(capteurs, predicate);
    }

    public static List<Capteur> filtrerParType(Stream<Capteur> capteurs, String type) {
        return filtrer(capteurs, capteur -> capteur.type().equals(type));
    }

    public static List<Capteur> filtrerParLocalisation(Stream<Capteur> capteurs, String localisation) {
        return filtrer(capteurs, capteur -> capteur.localisation().equals(localisation));
    }

    public static List<Capteur> filtrerParTypeEtLocalisation(Stream<Capteur> capteurs, String type, String localisation) {
        return filtrer(capteurs, capteur -> capteur.type().equals(type) && capteur.localisation().equals(localisation));
    }
}
