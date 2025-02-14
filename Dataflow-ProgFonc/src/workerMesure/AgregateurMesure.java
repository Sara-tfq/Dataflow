package workerMesure;

import source.Capteur;
import source.Mesure;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgregateurMesure {


    public static Map<String, DoubleSummaryStatistics> aggregatorMesuresParType(
            Stream<Mesure> mesuresStream,
            Map<String, Capteur> capteursMap) {
        return mesuresStream
                .filter(m -> capteursMap.containsKey(m.capteurId()))
                .collect(Collectors.groupingBy(
                        m -> capteursMap.get(m.capteurId()).type(),
                        Collectors.summarizingDouble(Mesure::valeur)
                ));
    }


}
