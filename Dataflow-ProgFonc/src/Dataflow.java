import producteur.MesureProducteur;
import source.Capteur;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Dataflow {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Executor service for parallel task execution
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // Step 1: Generate Sensors
        System.out.println("Step 1: Generating Sensors...");
        List<Capteur> capteurs = producteur.CapteurProducteur.genererCapteurs(5);
        capteurs.forEach(capteur -> System.out.println("Generated Sensor: " + capteur));

        // Step 2: Generate Measurements
        System.out.println("Step 2: Generating Measurements...");
        MesureProducteur producteur = new MesureProducteur();
        List<source.Mesure> mesures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mesures.add(producteur.produireMesure());
            Thread.sleep(500); // Simulate delay between measurements
        }
        mesures.forEach(mesure -> System.out.println("Generated Measure: " + mesure));

        // Step 3: Filter Sensors (Parallel)
        System.out.println("Step 3: Filtering Sensors (Parallel)...");
        Callable<List<Capteur>> filterTask = () -> workerCapteur.FiltreCapteur.filtrerParType(capteurs.stream(), "Température").collect(Collectors.toList());
        Future<List<Capteur>> filteredSensorsFuture = executorService.submit(filterTask);
        List<source.Capteur> filteredSensors = filteredSensorsFuture.get();
        filteredSensors.forEach(sensor -> System.out.println("Filtered Sensor: " + sensor));

        // Step 4: Aggregate Measurements (Parallel)
        System.out.println("Step 4: Aggregating Measurements (Parallel)...");
        Map<String, Capteur> capteurMap = capteurs.stream().collect(Collectors.toMap(source.Capteur::id, c -> c));
        Callable<Map<String, DoubleSummaryStatistics>> aggregateTask = () -> workerMesure.AgregateurMesure.aggregatorMesuresParType(mesures.stream(), capteurMap);
        Future<Map<String, DoubleSummaryStatistics>> aggregateFuture = executorService.submit(aggregateTask);
        Map<String, DoubleSummaryStatistics> aggregatedResults = aggregateFuture.get();
        aggregatedResults.forEach((key, value) -> System.out.println("Aggregation result for " + key + ": " + value));

        // Step 5: Transform Measurements (Sequential)
        System.out.println("Step 5: Transforming Measurements (Sequential)...");
        List<source.Mesure> transformedMeasurements = mesures.stream()
                .map(workerMesure.TransformMesure::celsiusToFahrenheit)
                .collect(Collectors.toList());
        transformedMeasurements.forEach(mesure -> System.out.println("Transformed Measure: " + mesure));

        // Step 6: CompletableFuture for Combined Execution (Sequential and Parallel Tasks)
        System.out.println("Step 6: Executing Combined Task with CompletableFuture...");
        CompletableFuture<Void> combinedFuture = CompletableFuture.runAsync(() -> {
            System.out.println("Executing combined future...");
            capteurs.forEach(System.out::println);
            mesures.forEach(System.out::println);
        }).thenRun(() -> {
            System.out.println("CompletableFuture completed!");
        });

        // Wait for all tasks to finish
        combinedFuture.get();

        // Shutdown Executor
        executorService.shutdown();
    }}