package workerMesure;

import org.junit.jupiter.api.Test;
import source.Capteur;
import source.Mesure;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AgregateurMesureTest {

    @Test
    void testAggregatorMesuresParType() {
        // Création d'une liste de capteurs
        List<Capteur> capteurs = Arrays.asList(
                new Capteur("1", "Température", "Paris"),
                new Capteur("2", "Humidité", "Lyon"),
                new Capteur("3", "Pression", "Marseille"),
                new Capteur("4", "Température", "Paris")
        );

        Map<String, Capteur> capteursMap = capteurs.stream()
                .collect(Collectors.toMap(Capteur::id, Function.identity()));


        List<Mesure> mesures = Arrays.asList(
                new Mesure("1", 25.0, LocalDateTime.now()),
                new Mesure("1", 30.0, LocalDateTime.now()),
                new Mesure("4", 20.0, LocalDateTime.now()),
                new Mesure("4", 22.0, LocalDateTime.now()),
                new Mesure("2", 50.0, LocalDateTime.now()),
                new Mesure("2", 45.0, LocalDateTime.now()),
                new Mesure("3", 101.0, LocalDateTime.now())
        );


        Map<String, DoubleSummaryStatistics> statsMap =
                AgregateurMesure.aggregatorMesuresParType(mesures.stream(), capteursMap);

        // Vérification que les clés attendues sont présentes
        assertTrue(statsMap.containsKey("Température"), "La map doit contenir le type Température");
        assertTrue(statsMap.containsKey("Humidité"), "La map doit contenir le type Humidité");
        assertTrue(statsMap.containsKey("Pression"), "La map doit contenir le type Pression");


        DoubleSummaryStatistics statsTemp = statsMap.get("Température");
        assertEquals(4, statsTemp.getCount(), "Nombre de mesures pour Température incorrect");
        assertEquals(97.0, statsTemp.getSum(), 0.0001, "Somme des mesures pour Température incorrecte");
        assertEquals(24.25, statsTemp.getAverage(), 0.0001, "Moyenne des mesures pour Température incorrecte");
        assertEquals(20.0, statsTemp.getMin(), 0.0001, "Valeur minimale pour Température incorrecte");
        assertEquals(30.0, statsTemp.getMax(), 0.0001, "Valeur maximale pour Température incorrecte");


        DoubleSummaryStatistics statsHum = statsMap.get("Humidité");
        assertEquals(2, statsHum.getCount(), "Nombre de mesures pour Humidité incorrect");
        assertEquals(95.0, statsHum.getSum(), 0.0001, "Somme des mesures pour Humidité incorrecte");
        assertEquals(47.5, statsHum.getAverage(), 0.0001, "Moyenne des mesures pour Humidité incorrecte");
        assertEquals(45.0, statsHum.getMin(), 0.0001, "Valeur minimale pour Humidité incorrecte");
        assertEquals(50.0, statsHum.getMax(), 0.0001, "Valeur maximale pour Humidité incorrecte");


        DoubleSummaryStatistics statsPress = statsMap.get("Pression");
        assertEquals(1, statsPress.getCount(), "Nombre de mesures pour Pression incorrect");
        assertEquals(101.0, statsPress.getSum(), 0.0001, "Somme des mesures pour Pression incorrecte");
        assertEquals(101.0, statsPress.getAverage(), 0.0001, "Moyenne des mesures pour Pression incorrecte");
        assertEquals(101.0, statsPress.getMin(), 0.0001, "Valeur minimale pour Pression incorrecte");
        assertEquals(101.0, statsPress.getMax(), 0.0001, "Valeur maximale pour Pression incorrecte");
    }
}
