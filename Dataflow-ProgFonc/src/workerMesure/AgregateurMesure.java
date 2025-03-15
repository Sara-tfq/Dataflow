package workerMesure;

import source.Capteur;
import source.Mesure;
import utils.Agregateur;

import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgregateurMesure {
    private static final Agregateur<Mesure, String, DoubleSummaryStatistics> agregateur = new Agregateur<>();

    public static Map<String, DoubleSummaryStatistics> aggregatorMesuresParType(
            Stream<Mesure> mesuresStream, Map<String, Capteur> capteursMap) {
        return agregateur.aggreger(
                mesuresStream.filter(m -> capteursMap.containsKey(m.capteurId())),
                m -> capteursMap.get(m.capteurId()).type(),
                Collectors.summarizingDouble(Mesure::valeur)
        );
    }
}
