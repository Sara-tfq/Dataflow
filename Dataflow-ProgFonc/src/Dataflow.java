//import producteur.CapteurProducteur;
//import producteur.MesureProducteur;
//import source.Capteur;
//import source.Mesure;
//import workerCapteur.FiltreCapteur;
//import workerMesure.AgregateurMesure;
//import workerMesure.FiltreMesure;
//import workerMesure.TransformMesure;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class Dataflow {
//    // Constantes pour les types de capteurs
//    private static final String TYPE_TEMPERATURE = "Température";
//    private static final String TYPE_HUMIDITE = "Humidité";
//    private static final String TYPE_PRESSION = "Pression";
//    private static final String TYPE_CO2 = "CO2";
//
//    // Constantes pour les localisations
//    private static final String LOC_BATIMENT_A = "Bâtiment A";
//    private static final String LOC_BATIMENT_B = "Bâtiment B";
//    private static final String LOC_SERRE = "Serre Extérieure";
//    private static final String LOC_AMPHI = "Amphithéâtre";
//    private static final String LOC_LABO = "Laboratoire";
//
//    public static void main(String[] args) {
//        // Démarrer la mesure du temps
//        Instant startTime = Instant.now();
//
//        // --- 1. Monitoring de début du dataflow
//        System.out.println("Début du dataflow : " + LocalDateTime.now() +
//                " | Thread: " + Thread.currentThread().getName());
//
//        // --- 2. PREMIÈRE SOURCE DE DONNÉES: Génération des capteurs
//        System.out.println("\n=== SOURCE 1: GÉNÉRATION DES CAPTEURS ===");
//        List<Capteur> capteurs = new ArrayList<>();
//
//        // Génération des capteurs par zone et type
//        capteurs.addAll(CapteurProducteur.genererCapteurs(20, TYPE_TEMPERATURE, LOC_BATIMENT_A));
//        capteurs.addAll(CapteurProducteur.genererCapteurs(10, TYPE_CO2, LOC_BATIMENT_A));
//        capteurs.addAll(CapteurProducteur.genererCapteurs(15, TYPE_CO2, LOC_AMPHI));
//        capteurs.addAll(CapteurProducteur.genererCapteurs(5, TYPE_PRESSION, LOC_LABO));
//        capteurs.addAll(CapteurProducteur.genererCapteurs(10, TYPE_HUMIDITE, LOC_SERRE));
//        capteurs.addAll(CapteurProducteur.genererCapteurs(5, TYPE_TEMPERATURE, LOC_SERRE));
//
//        System.out.println("Capteurs générés : " + capteurs.size());
//        Map<String, Long> capteurParType = capteurs.stream()
//                .collect(Collectors.groupingBy(c -> c.type() + " @ " + c.localisation(), Collectors.counting()));
//        capteurParType.forEach((k, v) -> System.out.println("- " + k + ": " + v));
//
//        // --- 3. DEUXIÈME SOURCE DE DONNÉES: Génération des mesures
//        System.out.println("\n=== SOURCE 2: GÉNÉRATION DES MESURES ===");
//        MesureProducteur mesureProducteur = new MesureProducteur(capteurs);
//        List<Mesure> mesures = mesureProducteur.produireMesures(100000);
//        System.out.println("Mesures générées : " + mesures.size());
//
//        // --- 4. Création d'une map d'accès rapide aux capteurs (id -> Capteur)
//        Map<String, Capteur> capteursMap = capteurs.stream()
//                .collect(Collectors.toMap(Capteur::id, Function.identity()));
//
//        // --- 5. FILTRAGE DES DONNÉES PAR ZONE (PARALLÈLE)
//        System.out.println("\n=== FILTRAGE PARALLÈLE DES DONNÉES PAR LOCALISATION ===");
//        ForkJoinPool customThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
//
//        try {
//            // Utiliser un ForkJoinPool pour paralléliser les opérations de filtrage
//            Map<String, List<Mesure>> mesuresParLocalisation = customThreadPool.submit(() ->
//                    mesures.parallelStream()
//                            .filter(m -> capteursMap.containsKey(m.capteurId()))
//                            .collect(Collectors.groupingBy(
//                                    m -> capteursMap.get(m.capteurId()).localisation(),
//                                    Collectors.toList()
//                            ))
//            ).get();
//
//            System.out.println("Mesures réparties par localisation:");
//            mesuresParLocalisation.forEach((loc, list) ->
//                    System.out.println("- " + loc + ": " + list.size() + " mesures"));
//
//            // --- 6. AGRÉGATION DES DONNÉES PAR TYPE DE CAPTEUR
//            System.out.println("\n=== AGRÉGATION PARALLÈLE DES DONNÉES PAR TYPE DE CAPTEUR ===");
//
//            Map<String, DoubleSummaryStatistics> statsGlobales = customThreadPool.submit(() ->
//                    mesures.parallelStream()
//                            .filter(m -> capteursMap.containsKey(m.capteurId()))
//                            .collect(Collectors.groupingBy(
//                                    m -> capteursMap.get(m.capteurId()).type(),
//                                    Collectors.summarizingDouble(Mesure::valeur)
//                            ))
//            ).get();
//
//            System.out.println("Statistiques globales par type de capteur:");
//            statsGlobales.forEach((type, stats) -> {
//                System.out.println("Type: " + type);
//                System.out.println("  - Moyenne: " + stats.getAverage());
//                System.out.println("  - Min: " + stats.getMin());
//                System.out.println("  - Max: " + stats.getMax());
//                System.out.println("  - Nombre de mesures: " + stats.getCount());
//            });
//
//            // --- 7. REDIVISION DES DONNÉES EN DEUX SOURCES (comme dans l'image)
//            System.out.println("\n=== REDIVISION DES DONNÉES EN DEUX POPULATIONS ===");
//
//            // Source 1: Population filtrée - Mesures critiques nécessitant des actions
//            List<Mesure> populationFiltered = customThreadPool.submit(() ->
//                    mesures.parallelStream()
//                            .filter(m -> {
//                                if (!capteursMap.containsKey(m.capteurId())) return false;
//                                Capteur c = capteursMap.get(m.capteurId());
//
//                                // Critères de filtrage selon le scénario
//                                if (TYPE_TEMPERATURE.equals(c.type()) && LOC_BATIMENT_A.equals(c.localisation()))
//                                    return m.valeur() > 28.0; // Température trop élevée
//                                else if (TYPE_CO2.equals(c.type()) && LOC_AMPHI.equals(c.localisation()))
//                                    return m.valeur() > 1100.0; // CO2 trop élevé
//                                else if (TYPE_HUMIDITE.equals(c.type()) && LOC_SERRE.equals(c.localisation()))
//                                    return m.valeur() < 18.0; // Humidité trop basse
//
//                                return false;
//                            })
//                            .collect(Collectors.toList())
//            ).get();
//
//            // Source 2: Population enrichie - Mesures transformées pour analyses complémentaires
//            List<Mesure> populationEnhanced = customThreadPool.submit(() -> {
//                List<Mesure> enhanced = new ArrayList<>();
//
//                // Transformation des températures en Fahrenheit
//                List<Mesure> tempFahrenheit = mesures.parallelStream()
//                        .filter(m -> capteursMap.containsKey(m.capteurId())
//                                && TYPE_TEMPERATURE.equals(capteursMap.get(m.capteurId()).type()))
//                        .map(TransformMesure::celsiusToFahrenheit)
//                        .collect(Collectors.toList());
//                enhanced.addAll(tempFahrenheit);
//
//                // Transformation des pressions en Bar
//                List<Mesure> pressionBar = mesures.parallelStream()
//                        .filter(m -> capteursMap.containsKey(m.capteurId())
//                                && TYPE_PRESSION.equals(capteursMap.get(m.capteurId()).type()))
//                        .map(TransformMesure::pascalToBar)
//                        .collect(Collectors.toList());
//                enhanced.addAll(pressionBar);
//
//                return enhanced;
//            }).get();
//
//            System.out.println("Population filtrée: " + populationFiltered.size() +
//                    " mesures nécessitant des actions");
//            System.out.println("Population enrichie: " + populationEnhanced.size() +
//                    " mesures transformées pour analyses");
//
//            // --- 8. ACTIONS CORRECTIVES BASÉES SUR LA POPULATION FILTRÉE
//            System.out.println("\n=== ACTIONS CORRECTIVES ===");
//
//            // Regrouper les mesures critiques par localisation et type
//            Map<String, List<Mesure>> actionsByLocation = populationFiltered.stream()
//                    .collect(Collectors.groupingBy(m -> {
//                        Capteur c = capteursMap.get(m.capteurId());
//                        return c.localisation() + " - " + c.type();
//                    }));
//
//            // Exécuter les actions correctives
//            actionsByLocation.forEach((locType, actionMesures) -> {
//                if (actionMesures.isEmpty()) return;
//
//                if (locType.startsWith(LOC_BATIMENT_A) && locType.contains(TYPE_TEMPERATURE)) {
//                    System.out.println("ACTION: Activation de la climatisation dans le Bâtiment A");
//                    System.out.println("  - " + actionMesures.size() + " alertes de température > 28°C");
//                    System.out.println("  - Température moyenne: " +
//                            actionMesures.stream().mapToDouble(Mesure::valeur).average().orElse(0) + "°C");
//                }
//                else if (locType.startsWith(LOC_AMPHI) && locType.contains(TYPE_CO2)) {
//                    System.out.println("ACTION: Renforcement de la ventilation dans l'Amphithéâtre");
//                    System.out.println("  - " + actionMesures.size() + " alertes de CO2 > 1100 ppm");
//                    System.out.println("  - CO2 moyen: " +
//                            actionMesures.stream().mapToDouble(Mesure::valeur).average().orElse(0) + " ppm");
//                }
//                else if (locType.startsWith(LOC_SERRE) && locType.contains(TYPE_HUMIDITE)) {
//                    System.out.println("ACTION: Démarrage du système d'arrosage dans la Serre");
//                    System.out.println("  - " + actionMesures.size() + " alertes d'humidité < 18%");
//                    System.out.println("  - Humidité moyenne: " +
//                            actionMesures.stream().mapToDouble(Mesure::valeur).average().orElse(0) + "%");
//                }
//            });
//
//            // --- 9. ANALYSES COMPLÉMENTAIRES BASÉES SUR LA POPULATION ENRICHIE
//            System.out.println("\n=== ANALYSES COMPLÉMENTAIRES ===");
//
//            // Statistiques des mesures transformées
//            Map<String, List<Mesure>> transformedByType = populationEnhanced.stream()
//                    .collect(Collectors.groupingBy(m -> {
//                        Capteur c = capteursMap.get(m.capteurId());
//                        return c.type();
//                    }));
//
//            transformedByType.forEach((type, typeMesures) -> {
//                if (typeMesures.isEmpty()) return;
//
//                if (type.equals(TYPE_TEMPERATURE)) {
//                    System.out.println("Analyse des températures en Fahrenheit:");
//                    System.out.println("  - Moyenne: " +
//                            typeMesures.stream().mapToDouble(Mesure::valeur).average().orElse(0) + " °F");
//                }
//                else if (type.equals(TYPE_PRESSION)) {
//                    System.out.println("Analyse des pressions en Bar:");
//                    System.out.println("  - Moyenne: " +
//                            typeMesures.stream().mapToDouble(Mesure::valeur).average().orElse(0) + " bar");
//                }
//            });
//
//        } catch (Exception e) {
//            System.err.println("Erreur lors du traitement parallèle: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            customThreadPool.shutdown();
//        }
//
//        // --- 10. RÉSUMÉ ET RAPPORT FINAL
//        System.out.println("\n=== RÉSUMÉ DES BÉNÉFICES ===");
//        System.out.println("- Réduction de la consommation énergétique grâce à l'optimisation des systèmes");
//        System.out.println("- Diminution des coûts d'exploitation");
//        System.out.println("- Réduction de l'empreinte écologique du campus");
//        System.out.println("- Amélioration du confort des étudiants et du personnel");
//
//        // Calcul et affichage du temps d'exécution total
//        Instant endTime = Instant.now();
//        Duration elapsed = Duration.between(startTime, endTime);
//        System.out.println("\n=== PERFORMANCE ===");
//        System.out.println("Temps d'exécution total: " + elapsed.toMillis() + " ms");
//        System.out.println("Nombre de processeurs utilisés: " + Runtime.getRuntime().availableProcessors());
//        System.out.println("Fin du dataflow: " + LocalDateTime.now());
//    }
//}