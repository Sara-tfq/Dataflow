import producteur.CapteurProducteur;
import producteur.MesureProducteur;
import source.Capteur;
import source.Mesure;
import workerMesure.AgregateurMesure;
import workerMesure.FiltreMesure;
import workerMesure.TransformMesure;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Dataflow {
    public static void main(String[] args) {
        // --- 1. Monitoring de début du dataflow
        System.out.println("Début du dataflow : " + LocalDateTime.now() +
                " | Thread: " + Thread.currentThread().getName());

        // --- 2. Génération des capteurs
        int nbCapteurs = 10;
        List<Capteur> capteurs = CapteurProducteur.genererCapteurs(nbCapteurs);
        System.out.println("\n--- Capteurs générés ---");
        capteurs.forEach(System.out::println);

        // --- 3. Génération des mesures à partir des capteurs générés
        // On passe la liste des capteurs à MesureProducteur
        MesureProducteur mesureProducteur = new MesureProducteur(capteurs);
        List<Mesure> mesures = mesureProducteur.produireMesures(50);
        System.out.println("\n--- Mesures générées ---");
        mesures.forEach(System.out::println);

        // --- 4. Filtrage des capteurs par type et localisation
        // Exemple : capteurs de type "Température" situés à "Paris"
        List<Capteur> capteursFiltres = capteurs.parallelStream()
                .filter(c -> "Température".equals(c.type()) && "Paris".equals(c.localisation()))
                .collect(Collectors.toList());
        System.out.println("\n--- Capteurs filtrés (Type: Température et Localisation: Paris) ---");
        capteursFiltres.forEach(System.out::println);

        // --- 5. Création d'une map d'accès rapide aux capteurs (id -> Capteur)
        Map<String, Capteur> capteursMap = capteurs.stream()
                .collect(Collectors.toMap(Capteur::id, c -> c));

        // --- 6. Filtrage des mesures en fonction du type et de seuils
        List<Mesure> mesuresTemperature = FiltreMesure.filtrerTemperature(
                mesures.stream(), capteursMap, 30.0, 10.0);
        System.out.println("\n--- Mesures de Température filtrées ---");
        mesuresTemperature.forEach(System.out::println);

        List<Mesure> mesuresHumidite = FiltreMesure.filtrerHumidite(
                mesures.stream(), capteursMap, 80.0, 20.0);
        System.out.println("\n--- Mesures d'Humidité filtrées ---");
        mesuresHumidite.forEach(System.out::println);

        List<Mesure> mesuresPression = FiltreMesure.filtrerPression(
                mesures.stream(), capteursMap, 105.0, 95.0);
        System.out.println("\n--- Mesures de Pression filtrées ---");
        mesuresPression.forEach(System.out::println);

        // --- 7. Transformation des mesures (exemple : conversion de Celsius en Fahrenheit)
        List<Mesure> mesuresTransfTemperature = mesuresTemperature.parallelStream()
                .map(TransformMesure::celsiusToFahrenheit)
                .collect(Collectors.toList());
        System.out.println("\n--- Mesures de Température transformées (Fahrenheit) ---");
        mesuresTransfTemperature.forEach(System.out::println);

        // --- 8. Agrégation des mesures par type
        Map<String, DoubleSummaryStatistics> statsParType =
                AgregateurMesure.aggregatorMesuresParType(mesures.stream(), capteursMap);
        System.out.println("\n--- Agrégation des mesures par type ---");
        statsParType.forEach((type, stats) -> System.out.println("Type: " + type + " => " + stats));

        // --- 9. Fusionner toutes les mesures filtrées en une seule source de données
        List<Mesure> sourceUnique = new ArrayList<>();
        sourceUnique.addAll(mesuresTemperature);
        sourceUnique.addAll(mesuresHumidite);
        sourceUnique.addAll(mesuresPression);
        System.out.println("\n--- Source unique (fusion de toutes les mesures filtrées) ---");
        sourceUnique.forEach(System.out::println);

        // --- 10. Séparation de la source unique en deux sources (par index pair/impair)
        Map<Boolean, List<Mesure>> partitions = sourceUnique.stream()
                .collect(Collectors.partitioningBy(m -> sourceUnique.indexOf(m) % 2 == 0));
        System.out.println("\n--- Source partitionnée ---");
        System.out.println("Partition (index pair) :");
        partitions.get(true).forEach(System.out::println);
        System.out.println("Partition (index impair) :");
        partitions.get(false).forEach(System.out::println);

        // --- 11. Traitement parallèle avec ForkJoinPool
        System.out.println("\n--- Traitement parallèle avec ForkJoinPool ---");
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        try {
            forkJoinPool.submit(() ->
                    sourceUnique.parallelStream().forEach(m ->
                            System.out.println("Traitement de mesure : " + m +
                                    " | Thread: " + Thread.currentThread().getName())
                    )
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            forkJoinPool.shutdown();
        }

        // --- 12. Monitoring de fin du dataflow
        System.out.println("\nFin du dataflow : " + LocalDateTime.now() +
                " | Thread: " + Thread.currentThread().getName());
    }
}
