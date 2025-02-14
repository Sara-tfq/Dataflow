package workerMesure;

import org.junit.jupiter.api.Test;
import source.Capteur;
import source.Mesure;
import workerMesure.FiltreMesure;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FiltreMesureTest {

    @Test
    void testFiltrerTemperature() {
        // Création de capteurs
        Capteur capteur1 = new Capteur("1", "Température", "Paris");
        Capteur capteur2 = new Capteur("2", "Température", "Lyon");
        Capteur capteur3 = new Capteur("3", "Humidité", "Marseille");
        List<Capteur> capteurs = Arrays.asList(capteur1, capteur2, capteur3);
        Map<String, Capteur> capteursMap = capteurs.stream()
                .collect(Collectors.toMap(Capteur::id, c -> c));


        Mesure m1 = new Mesure("1", 35.0, LocalDateTime.now());  // > 30.0 => retenue
        Mesure m2 = new Mesure("1", 8.0, LocalDateTime.now());   // < 10.0 => retenue
        Mesure m3 = new Mesure("1", 20.0, LocalDateTime.now());  // entre 10 et 30 => rejetée
        Mesure m4 = new Mesure("2", 9.5, LocalDateTime.now());   // < 10.0 => retenue
        Mesure m5 = new Mesure("2", 30.5, LocalDateTime.now());  // > 30.0 => retenue
        Mesure m6 = new Mesure("3", 50.0, LocalDateTime.now());  // Capteur de type "Humidité" => rejetée
        List<Mesure> mesures = Arrays.asList(m1, m2, m3, m4, m5, m6);


        List<Mesure> filteredTemperature = FiltreMesure.filtrerTemperature(
                mesures.stream(), capteursMap, 30.0, 10.0);

        assertEquals(4, filteredTemperature.size(), "Le nombre de mesures filtrées doit être 4");
        assertTrue(filteredTemperature.contains(m1));
        assertTrue(filteredTemperature.contains(m2));
        assertTrue(filteredTemperature.contains(m4));
        assertTrue(filteredTemperature.contains(m5));
        assertFalse(filteredTemperature.contains(m3));
        assertFalse(filteredTemperature.contains(m6));
    }

    @Test
    void testFiltrerHumidite() {

        Capteur capteur1 = new Capteur("1", "Humidité", "Paris");
        Capteur capteur2 = new Capteur("2", "Humidité", "Lyon");
        Capteur capteur3 = new Capteur("3", "Température", "Marseille");
        List<Capteur> capteurs = Arrays.asList(capteur1, capteur2, capteur3);
        Map<String, Capteur> capteursMap = capteurs.stream()
                .collect(Collectors.toMap(Capteur::id, c -> c));

        Mesure m1 = new Mesure("1", 85.0, LocalDateTime.now());  // > 80.0 => retenue
        Mesure m2 = new Mesure("1", 15.0, LocalDateTime.now());  // < 20.0 => retenue
        Mesure m3 = new Mesure("1", 50.0, LocalDateTime.now());  // entre 20 et 80 => rejetée
        Mesure m4 = new Mesure("2", 90.0, LocalDateTime.now());  // > 80.0 => retenue
        Mesure m5 = new Mesure("3", 10.0, LocalDateTime.now());  // Type "Température" => rejetée
        List<Mesure> mesures = Arrays.asList(m1, m2, m3, m4, m5);

        List<Mesure> filteredHumidite = FiltreMesure.filtrerHumidite(
                mesures.stream(), capteursMap, 80.0, 20.0);

        // Vérification du résultat : on attend que m1, m2 et m4 soient retenues
        assertEquals(3, filteredHumidite.size(), "Le nombre de mesures filtrées doit être 3");
        assertTrue(filteredHumidite.contains(m1));
        assertTrue(filteredHumidite.contains(m2));
        assertTrue(filteredHumidite.contains(m4));
        assertFalse(filteredHumidite.contains(m3));
        assertFalse(filteredHumidite.contains(m5));
    }

    @Test
    void testFiltrerPression() {
        // Création de capteurs
        Capteur capteur1 = new Capteur("1", "Pression", "Paris");
        Capteur capteur2 = new Capteur("2", "Pression", "Lyon");
        Capteur capteur3 = new Capteur("3", "Température", "Marseille");
        List<Capteur> capteurs = Arrays.asList(capteur1, capteur2, capteur3);
        Map<String, Capteur> capteursMap = capteurs.stream()
                .collect(Collectors.toMap(Capteur::id, c -> c));

        // Création de mesures pour le filtrage de la pression
        // Seuils : supThreshold = 105.0 et infThreshold = 95.0
        // On retient les mesures > 105.0 ou < 95.0.
        Mesure m1 = new Mesure("1", 110.0, LocalDateTime.now());  // > 105.0 => retenue
        Mesure m2 = new Mesure("1", 90.0, LocalDateTime.now());   // < 95.0 => retenue
        Mesure m3 = new Mesure("1", 100.0, LocalDateTime.now());  // entre 95 et 105 => rejetée
        Mesure m4 = new Mesure("2", 106.0, LocalDateTime.now());  // > 105.0 => retenue
        Mesure m5 = new Mesure("3", 80.0, LocalDateTime.now());   // Type "Température" => rejetée
        List<Mesure> mesures = Arrays.asList(m1, m2, m3, m4, m5);

        List<Mesure> filteredPression = FiltreMesure.filtrerPression(
                mesures.stream(), capteursMap, 105.0, 95.0);

        // Vérification du résultat : on attend que m1, m2 et m4 soient retenues
        assertEquals(3, filteredPression.size(), "Le nombre de mesures filtrées doit être 3");
        assertTrue(filteredPression.contains(m1));
        assertTrue(filteredPression.contains(m2));
        assertTrue(filteredPression.contains(m4));
        assertFalse(filteredPression.contains(m3));
        assertFalse(filteredPression.contains(m5));
    }
}
