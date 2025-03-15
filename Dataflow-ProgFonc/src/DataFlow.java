import source.Capteur;
import source.Mesure;
import producteur.CapteurProducteur;
import producteur.MesureProducteur;
import utils.Producteur;
import workerCapteur.FiltreCapteur;
import workerMesure.TransformMesure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataFlow {
    public static void main(String[] args) {
        // Heure de démarrage
        LocalDateTime startTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("========== DÉMARRAGE DU DATAFLOW ==========");
        System.out.println("Date/heure: " + startTime.format(formatter));
        System.out.println("Thread principal: " + Thread.currentThread().getName());

        // Création du ForkJoinPool custom pour un meilleur contrôle du parallélisme
        ForkJoinPool customThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        System.out.println("\n========== DÉPLOIEMENT DES CAPTEURS (SOURCE 1) ==========");

        // SOURCE 1: Génération des capteurs pour tout le campus
        Producteur<Capteur> capteurProducteur = CapteurProducteur.creerProducteur();
        List<Capteur> tousLesCapteurs = capteurProducteur.produire(100);

        // Création d'une map pour accéder rapidement aux capteurs par leur ID
        Map<String, Capteur> capteurMap = tousLesCapteurs.stream()
                .collect(Collectors.toConcurrentMap(Capteur::id, Function.identity()));

        System.out.println("SOURCE 1: Nombre total de capteurs déployés: " + tousLesCapteurs.size());

        // Affichage de quelques exemples de capteurs
        System.out.println("\nExemples de capteurs déployés:");
        tousLesCapteurs.stream()
                .limit(5)
                .forEach(c -> System.out.println("  - ID: " + c.id() + ", Type: " + c.type() + ", Localisation: " + c.localisation()));

        // Filtrage des capteurs par bâtiment et type (en parallèle)
        System.out.println("\nRépartition des capteurs par localisation:");

        // POINT DE BIFURCATION 1: Le flux de capteurs se divise par localisation
        System.out.println("\n*** POINT DE BIFURCATION 1: Répartition par localisation ***");

        CompletableFuture<List<Capteur>> capteursBatimentAFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Traitement des capteurs du Bâtiment A");
            return FiltreCapteur.filtrerParLocalisation(tousLesCapteurs.stream(), "Bâtiment A");
        }, customThreadPool);

        CompletableFuture<List<Capteur>> capteursBatimentBFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Traitement des capteurs du Bâtiment B");
            return FiltreCapteur.filtrerParLocalisation(tousLesCapteurs.stream(), "Bâtiment B");
        }, customThreadPool);

        CompletableFuture<List<Capteur>> capteursSerreFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Traitement des capteurs de la Serre");
            return FiltreCapteur.filtrerParLocalisation(tousLesCapteurs.stream(), "Serre Extérieure");
        }, customThreadPool);

        // Attente de la fin du filtrage des capteurs
        List<Capteur> capteursBatimentA = capteursBatimentAFuture.join();
        List<Capteur> capteursBatimentB = capteursBatimentBFuture.join();
        List<Capteur> capteursSerre = capteursSerreFuture.join();

        // Filtrage des capteurs par type dans chaque bâtiment
        Map<String, List<Capteur>> capteursBatimentAParType = capteursBatimentA.stream()
                .collect(Collectors.groupingBy(Capteur::type));

        Map<String, List<Capteur>> capteursBatimentBParType = capteursBatimentB.stream()
                .collect(Collectors.groupingBy(Capteur::type));

        Map<String, List<Capteur>> capteursSerreParType = capteursSerre.stream()
                .collect(Collectors.groupingBy(Capteur::type));

        // Affichage des statistiques des capteurs déployés
        System.out.println("\nBâtiment A - Capteurs: " + capteursBatimentA.size());
        capteursBatimentAParType.forEach((type, capteurs) ->
                System.out.println("  - " + type + ": " + capteurs.size()));

        System.out.println("\nBâtiment B - Capteurs: " + capteursBatimentB.size());
        capteursBatimentBParType.forEach((type, capteurs) ->
                System.out.println("  - " + type + ": " + capteurs.size()));

        System.out.println("\nSerre Extérieure - Capteurs: " + capteursSerre.size());
        capteursSerreParType.forEach((type, capteurs) ->
                System.out.println("  - " + type + ": " + capteurs.size()));

        System.out.println("\n========== COLLECTE DES DONNÉES (SOURCE 2) ==========");

        // SOURCE 2: Création du producteur de mesures et génération des mesures
        System.out.println("Génération des mesures pour tous les capteurs...");
        Producteur<Mesure> mesureProducteur = MesureProducteur.creerProducteur(tousLesCapteurs);
        List<Mesure> toutesMesures = mesureProducteur.produire(5000);  // Augmentation du nombre de mesures

        System.out.println("SOURCE 2: Nombre total de mesures collectées: " + toutesMesures.size());

        // Affichage de quelques exemples de mesures
        System.out.println("\nExemples de mesures collectées:");
        toutesMesures.stream()
                .limit(5)
                .forEach(m -> {
                    Capteur c = capteurMap.get(m.capteurId());
                    System.out.println("  - Capteur: " + m.capteurId().substring(0, 8) +
                            ", Type: " + (c != null ? c.type() : "Inconnu") +
                            ", Valeur: " + m.valeur() +
                            ", Localisation: " + (c != null ? c.localisation() : "Inconnue"));
                });

        // POINT DE BIFURCATION 2: Le flux de mesures se divise pour des analyses parallèles
        System.out.println("\n*** POINT DE BIFURCATION 2: Analyses parallèles des mesures ***");

        // SOURCE 3 (dérivée): Création de mesures supplémentaires pour simuler des pics anormaux
        System.out.println("\n========== GÉNÉRATION DE DONNÉES SUPPLÉMENTAIRES (SOURCE 3) ==========");
        List<Mesure> mesuresAnormales = new ArrayList<>();

        // Ajout de températures anormales (trop élevées)
        capteursBatimentAParType.getOrDefault("Température", new ArrayList<>()).stream()
                .limit(3)
                .forEach(c -> mesuresAnormales.add(new Mesure(c.id(), 32.5))); // Températures élevées

        // Ajout de niveaux de CO2 anormaux
        capteursBatimentBParType.getOrDefault("CO2", new ArrayList<>()).stream()
                .limit(3)
                .forEach(c -> mesuresAnormales.add(new Mesure(c.id(), 1200))); // CO2 élevé

        // Ajout de niveaux d'humidité anormaux
        capteursSerreParType.getOrDefault("Humidité", new ArrayList<>()).stream()
                .limit(3)
                .forEach(c -> mesuresAnormales.add(new Mesure(c.id(), 15))); // Humidité basse

        System.out.println("SOURCE 3: Nombre de mesures anormales générées: " + mesuresAnormales.size());

        // Fusion des sources de données
        System.out.println("\n*** POINT DE FUSION: Combinaison des sources de mesures ***");
        List<Mesure> toutesLesMesuresCombinees = new ArrayList<>(toutesMesures);
        toutesLesMesuresCombinees.addAll(mesuresAnormales);
        System.out.println("Nombre total de mesures après fusion: " + toutesLesMesuresCombinees.size());

        System.out.println("\n========== ANALYSE DES DONNÉES ==========");

        // Utilisation de CompletableFuture pour les traitements parallèles
        // Analyse des températures dans le Bâtiment A
        CompletableFuture<Void> analyseTemperatureBatAFuture = CompletableFuture.runAsync(() -> {
            try {
                customThreadPool.submit(() -> {
                    System.out.println("\n--- Analyse de température - Bâtiment A ---");

                    // Identification des capteurs de température dans le bâtiment A
                    List<Capteur> capteursTemperatureBatA = FiltreCapteur.filtrerParTypeEtLocalisation(
                            tousLesCapteurs.stream(), "Température", "Bâtiment A");

                    if (capteursTemperatureBatA.isEmpty()) {
                        System.out.println("Aucun capteur de température trouvé dans le Bâtiment A");
                        // Ajouter manuellement un capteur si nécessaire
                        capteursTemperatureBatA = List.of(Capteur.generate("Température", "Bâtiment A"));
                        System.out.println("Capteur de température ajouté manuellement: " + capteursTemperatureBatA.get(0));
                    }

                    // Filtrage des mesures pour ces capteurs
                    List<Capteur> finalCapteursTemperatureBatA = capteursTemperatureBatA;
                    List<Mesure> mesuresTemperatureBatA = toutesLesMesuresCombinees.stream()
                            .filter(m -> finalCapteursTemperatureBatA.stream()
                                    .anyMatch(c -> c.id().equals(m.capteurId())))
                            .collect(Collectors.toList());

                    // Si aucune mesure n'est trouvée, en générer
                    if (mesuresTemperatureBatA.isEmpty()) {
                        System.out.println("Aucune mesure de température trouvée pour le Bâtiment A, génération de mesures...");
                        for (Capteur c : capteursTemperatureBatA) {
                            mesuresTemperatureBatA.add(new Mesure(c.id(), 20 + Math.random() * 10));  // Entre 20 et 30°C
                            mesuresTemperatureBatA.add(new Mesure(c.id(), 20 + Math.random() * 10));
                            mesuresTemperatureBatA.add(new Mesure(c.id(), 32.5));  // Une valeur anormale
                        }
                    }

                    System.out.println("Mesures collectées pour la température (Bâtiment A): " + mesuresTemperatureBatA.size());
                    mesuresTemperatureBatA.forEach(m ->
                            System.out.println("  - Capteur " + m.capteurId().substring(0, 8) + ": " + m.valeur() + " °C"));

                    // Agrégation des mesures de température
                    DoubleSummaryStatistics stats = mesuresTemperatureBatA.stream()
                            .collect(Collectors.summarizingDouble(Mesure::valeur));

                    System.out.println("Température moyenne: " + String.format("%.1f", stats.getAverage()) + " °C");
                    System.out.println("Température minimale: " + String.format("%.1f", stats.getMin()) + " °C");
                    System.out.println("Température maximale: " + String.format("%.1f", stats.getMax()) + " °C");

                    // Détection des températures anormales (> 28°C ou < 18°C)
                    List<Mesure> temperaturesAnormales = mesuresTemperatureBatA.stream()
                            .filter(m -> m.valeur() > 28 || m.valeur() < 18)
                            .collect(Collectors.toList());

                    if (!temperaturesAnormales.isEmpty()) {
                        System.out.println("Alertes de température détectées (" + temperaturesAnormales.size() + "):");
                        temperaturesAnormales.forEach(m ->
                                System.out.println("  - Capteur " + m.capteurId().substring(0, 8) +
                                        ": " + String.format("%.1f", m.valeur()) + " °C"));

                        System.out.println("Action: Ajustement automatique du système de climatisation");
                    } else {
                        System.out.println("Température stable. Maintien du système de chauffage/climatisation actuel");
                    }
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, customThreadPool);

        // Analyse du CO2 dans le Bâtiment B (amphithéâtre)
        CompletableFuture<Void> analyseCO2BatBFuture = CompletableFuture.runAsync(() -> {
            try {
                customThreadPool.submit(() -> {
                    System.out.println("\n--- Analyse de CO2 - Bâtiment B (Amphithéâtre) ---");

                    // Identification des capteurs de CO2 dans le bâtiment B
                    List<Capteur> capteursCO2BatB = FiltreCapteur.filtrerParTypeEtLocalisation(
                            tousLesCapteurs.stream(), "CO2", "Bâtiment B");

                    if (capteursCO2BatB.isEmpty()) {
                        System.out.println("Aucun capteur de CO2 trouvé dans le Bâtiment B");
                        // Ajouter manuellement un capteur si nécessaire
                        capteursCO2BatB = List.of(Capteur.generate("CO2", "Bâtiment B"));
                        System.out.println("Capteur de CO2 ajouté manuellement: " + capteursCO2BatB.get(0));
                    }

                    // Filtrage des mesures pour ces capteurs
                    List<Capteur> finalCapteursCO2BatB = capteursCO2BatB;
                    List<Mesure> mesuresCO2BatB = toutesLesMesuresCombinees.stream()
                            .filter(m -> finalCapteursCO2BatB.stream()
                                    .anyMatch(c -> c.id().equals(m.capteurId())))
                            .collect(Collectors.toList());

                    // Si aucune mesure n'est trouvée, en générer
                    if (mesuresCO2BatB.isEmpty()) {
                        System.out.println("Aucune mesure de CO2 trouvée pour le Bâtiment B, génération de mesures...");
                        for (Capteur c : capteursCO2BatB) {
                            mesuresCO2BatB.add(new Mesure(c.id(), 600 + Math.random() * 300));  // Entre 600 et 900 ppm
                            mesuresCO2BatB.add(new Mesure(c.id(), 600 + Math.random() * 300));
                            mesuresCO2BatB.add(new Mesure(c.id(), 1200));  // Une valeur anormale
                        }
                    }

                    System.out.println("Mesures collectées pour le CO2 (Bâtiment B): " + mesuresCO2BatB.size());
                    mesuresCO2BatB.forEach(m ->
                            System.out.println("  - Capteur " + m.capteurId().substring(0, 8) + ": " + m.valeur() + " ppm"));

                    // Filtrage des mesures de CO2 dépassant 1000 ppm
                    List<Mesure> co2Elevees = mesuresCO2BatB.stream()
                            .filter(m -> m.valeur() > 1000)
                            .collect(Collectors.toList());

                    if (!co2Elevees.isEmpty()) {
                        System.out.println("Alertes de CO2 élevé détectées (" + co2Elevees.size() + "):");
                        co2Elevees.forEach(m ->
                                System.out.println("  - Capteur " + m.capteurId().substring(0, 8) +
                                        ": " + String.format("%.1f", m.valeur()) + " ppm"));

                        System.out.println("Action: Activation immédiate du système de ventilation");
                    }

                    // Agrégation des mesures de CO2
                    DoubleSummaryStatistics stats = mesuresCO2BatB.stream()
                            .collect(Collectors.summarizingDouble(Mesure::valeur));

                    System.out.println("CO2 moyen: " + String.format("%.1f", stats.getAverage()) + " ppm");
                    System.out.println("CO2 minimal: " + String.format("%.1f", stats.getMin()) + " ppm");
                    System.out.println("CO2 maximal: " + String.format("%.1f", stats.getMax()) + " ppm");
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, customThreadPool);

        // Analyse de l'humidité dans la Serre Extérieure
        CompletableFuture<Void> analyseHumiditeSerre = CompletableFuture.runAsync(() -> {
            try {
                customThreadPool.submit(() -> {
                    System.out.println("\n--- Analyse d'Humidité - Serre Extérieure ---");

                    // Identification des capteurs d'humidité dans la serre
                    List<Capteur> capteursHumiditeSerre = FiltreCapteur.filtrerParTypeEtLocalisation(
                            tousLesCapteurs.stream(), "Humidité", "Serre Extérieure");

                    if (capteursHumiditeSerre.isEmpty()) {
                        System.out.println("Aucun capteur d'humidité trouvé dans la Serre Extérieure");
                        // Ajouter manuellement un capteur si nécessaire
                        capteursHumiditeSerre = List.of(Capteur.generate("Humidité", "Serre Extérieure"));
                        System.out.println("Capteur d'humidité ajouté manuellement: " + capteursHumiditeSerre.get(0));
                    }

                    // Filtrage des mesures pour ces capteurs
                    List<Capteur> finalCapteursHumiditeSerre = capteursHumiditeSerre;
                    List<Mesure> mesuresHumiditeSerre = toutesLesMesuresCombinees.stream()
                            .filter(m -> finalCapteursHumiditeSerre.stream()
                                    .anyMatch(c -> c.id().equals(m.capteurId())))
                            .collect(Collectors.toList());

                    // Si aucune mesure n'est trouvée, en générer
                    if (mesuresHumiditeSerre.isEmpty()) {
                        System.out.println("Aucune mesure d'humidité trouvée pour la Serre, génération de mesures...");
                        for (Capteur c : capteursHumiditeSerre) {
                            mesuresHumiditeSerre.add(new Mesure(c.id(), 40 + Math.random() * 30));  // Entre 40 et 70%
                            mesuresHumiditeSerre.add(new Mesure(c.id(), 40 + Math.random() * 30));
                            mesuresHumiditeSerre.add(new Mesure(c.id(), 15));  // Une valeur anormalement basse
                        }
                    }

                    System.out.println("Mesures collectées pour l'humidité (Serre Extérieure): " + mesuresHumiditeSerre.size());
                    mesuresHumiditeSerre.forEach(m ->
                            System.out.println("  - Capteur " + m.capteurId().substring(0, 8) + ": " + m.valeur() + " %"));

                    // Détection des niveaux d'humidité bas (< 30%)
                    List<Mesure> humiditeBasse = mesuresHumiditeSerre.stream()
                            .filter(m -> m.valeur() < 30)
                            .collect(Collectors.toList());

                    if (!humiditeBasse.isEmpty()) {
                        System.out.println("Alertes d'humidité basse détectées (" + humiditeBasse.size() + "):");
                        humiditeBasse.forEach(m ->
                                System.out.println("  - Capteur " + m.capteurId().substring(0, 8) +
                                        ": " + String.format("%.1f", m.valeur()) + " %"));

                        System.out.println("Action: Activation du système d'arrosage automatique");
                    }

                    // Agrégation des mesures d'humidité
                    DoubleSummaryStatistics stats = mesuresHumiditeSerre.stream()
                            .collect(Collectors.summarizingDouble(Mesure::valeur));

                    System.out.println("Humidité moyenne: " + String.format("%.1f", stats.getAverage()) + " %");
                    System.out.println("Humidité minimale: " + String.format("%.1f", stats.getMin()) + " %");
                    System.out.println("Humidité maximale: " + String.format("%.1f", stats.getMax()) + " %");
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, customThreadPool);

        // Analyse de la pression dans le Bâtiment B (laboratoire)
        CompletableFuture<Void> analysePressionBatB = CompletableFuture.runAsync(() -> {
            try {
                customThreadPool.submit(() -> {
                    System.out.println("\n--- Analyse de Pression - Bâtiment B (Laboratoire) ---");

                    // Identification des capteurs de pression dans le bâtiment B
                    List<Capteur> capteursPressionBatB = FiltreCapteur.filtrerParTypeEtLocalisation(
                            tousLesCapteurs.stream(), "Pression", "Bâtiment B");

                    if (capteursPressionBatB.isEmpty()) {
                        System.out.println("Aucun capteur de pression trouvé dans le Bâtiment B");
                        // Ajouter manuellement un capteur si nécessaire
                        capteursPressionBatB = List.of(Capteur.generate("Pression", "Bâtiment B"));
                        System.out.println("Capteur de pression ajouté manuellement: " + capteursPressionBatB.get(0));
                    }

                    // Filtrage des mesures pour ces capteurs
                    List<Capteur> finalCapteursPressionBatB = capteursPressionBatB;
                    List<Mesure> mesuresPressionBatB = toutesLesMesuresCombinees.stream()
                            .filter(m -> finalCapteursPressionBatB.stream()
                                    .anyMatch(c -> c.id().equals(m.capteurId())))
                            .collect(Collectors.toList());

                    // Si aucune mesure n'est trouvée, en générer
                    if (mesuresPressionBatB.isEmpty()) {
                        System.out.println("Aucune mesure de pression trouvée pour le Bâtiment B, génération de mesures...");
                        for (Capteur c : capteursPressionBatB) {
                            mesuresPressionBatB.add(new Mesure(c.id(), 101000 + Math.random() * 1000));  // Environ 1010 hPa
                            mesuresPressionBatB.add(new Mesure(c.id(), 101000 + Math.random() * 1000));
                        }
                    }

                    System.out.println("Mesures collectées pour la pression (Bâtiment B): " + mesuresPressionBatB.size());
                    mesuresPressionBatB.forEach(m ->
                            System.out.println("  - Capteur " + m.capteurId().substring(0, 8) + ": " + m.valeur() + " Pa"));

                    // Transformation des mesures de Pascal en Bar avec TransformMesure
                    List<Mesure> mesuresEnBar = mesuresPressionBatB.stream()
                            .map(m -> new Mesure(m.capteurId(), m.valeur() / 100000)) // Conversion Pa en bar
                            .collect(Collectors.toList());

                    System.out.println("Mesures transformées en bar:");
                    mesuresEnBar.forEach(m ->
                            System.out.println("  - Capteur " + m.capteurId().substring(0, 8) + ": " +
                                    String.format("%.5f", m.valeur()) + " bar"));

                    // Agrégation des mesures de pression
                    DoubleSummaryStatistics stats = mesuresPressionBatB.stream()
                            .collect(Collectors.summarizingDouble(Mesure::valeur));

                    System.out.println("Pression moyenne: " + String.format("%.1f", stats.getAverage()) + " Pa");
                    System.out.println("Pression minimale: " + String.format("%.1f", stats.getMin()) + " Pa");
                    System.out.println("Pression maximale: " + String.format("%.1f", stats.getMax()) + " Pa");
                }).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, customThreadPool);

        // POINT DE FUSION FINALE: Attente de la fin de toutes les analyses
        System.out.println("\n*** POINT DE FUSION FINALE: Attente de la fin de toutes les analyses ***");
        CompletableFuture.allOf(
                analyseTemperatureBatAFuture,
                analyseCO2BatBFuture,
                analyseHumiditeSerre,
                analysePressionBatB
        ).join();

        // Calcul de la durée d'exécution
        LocalDateTime endTime = LocalDateTime.now();
        long durationInSeconds = java.time.Duration.between(startTime, endTime).getSeconds();

        System.out.println("\n========== RAPPORT FINAL ==========");
        System.out.println("Date/heure de fin: " + endTime.format(formatter));
        System.out.println("Durée totale d'exécution: " + durationInSeconds + " secondes");
        System.out.println("Nombre de capteurs traités: " + tousLesCapteurs.size());
        System.out.println("Nombre de mesures analysées: " + toutesLesMesuresCombinees.size());

        // Résumé des alertes détectées
        System.out.println("\nRésumé des alertes et actions:");
        System.out.println("1. Bâtiment A: Suivi des températures anormales et ajustement climatisation");
        System.out.println("2. Bâtiment B (Amphithéâtre): Surveillance du CO2 et activation ventilation si nécessaire");
        System.out.println("3. Bâtiment B (Laboratoire): Maintien de la stabilité de pression");
        System.out.println("4. Serre Extérieure: Gestion de l'humidité et arrosage automatique si nécessaire");

        // Fermeture du pool de threads personnalisé
        customThreadPool.shutdown();

        System.out.println("\n========== FIN DU DATAFLOW ==========");
    }
}