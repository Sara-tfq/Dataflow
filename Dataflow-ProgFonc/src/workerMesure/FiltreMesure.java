package workerMesure;

import source.Capteur;
import source.Mesure;
import utils.Filtre;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FiltreMesure {
    private static final Filtre<Mesure> filtre = new Filtre<>();

    public static List<Mesure> filtrerParType(Stream<Mesure> mesures,
                                              Map<String, Capteur> capteursMap,
                                              String type,
                                              double supThreshold,
                                              double infThreshold) {
        return filtre.filtrer(mesures, m -> {
            Capteur capteur = capteursMap.get(m.capteurId());
            return capteur != null && capteur.type().equals(type) &&
                    (m.valeur() > supThreshold || m.valeur() < infThreshold);
        });
    }
}
