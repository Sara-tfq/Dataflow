package workerMesure;

import source.Capteur;
import source.Mesure;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FiltreMesure {

    public static List<Mesure> filtrerTemperature(Stream<Mesure> mesures,
                                                  Map<String, Capteur> capteursMap,
                                                  double supThreshold,
                                                  double infThreshold) {
        return mesures
                .filter(m -> {
                    Capteur capteur = capteursMap.get(m.capteurId());
                    return capteur != null && "Température".equals(capteur.type());
                })
                .filter(m -> m.valeur() > supThreshold || m.valeur() < infThreshold)
                .toList();
    }

    public static List<Mesure> filtrerHumidite(Stream<Mesure> mesures,
                                               Map<String, Capteur> capteursMap,
                                               double supThreshold,
                                               double infThreshold) {
        return mesures
                .filter(m -> {
                    Capteur capteur = capteursMap.get(m.capteurId());
                    return capteur != null && "Humidité".equals(capteur.type());
                })
                .filter(m -> m.valeur() > supThreshold || m.valeur() < infThreshold)
                .toList();
    }

    public static List<Mesure> filtrerPression(Stream<Mesure> mesures,
                                               Map<String, Capteur> capteursMap,
                                               double supThreshold,
                                               double infThreshold) {
        return mesures
                .filter(m -> {
                    Capteur capteur = capteursMap.get(m.capteurId());
                    return capteur != null && "Pression".equals(capteur.type());
                })
                .filter(m -> m.valeur() > supThreshold || m.valeur() < infThreshold)
                .toList();
    }





}
