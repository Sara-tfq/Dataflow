package workerCapteur;

import source.Capteur;

import java.util.stream.Stream;

public class FiltreCapteur {
    public static Stream<Capteur> filtrerParType(Stream<Capteur> capteurs, String type) {
        return capteurs.filter(capteur -> capteur.type().equals(type));
    }

    public static Stream<Capteur> filtrerParLocalisation(Stream<Capteur> capteurs, String localisation) {
        return capteurs.filter(capteur -> capteur.localisation().equals(localisation));
    }

    public static Stream<Capteur> filtrerParTypeEtLocalisation(Stream<Capteur> capteurs, String type, String localisation) {
        return capteurs.filter(capteur -> capteur.type().equals(type) && capteur.localisation().equals(localisation));
    }
}
