package workerMesure;

import org.junit.jupiter.api.Test;
import source.Capteur;
import source.Mesure;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FiltreMesureTest {

    @Test
    void testFiltrerTemperature() {
        Capteur capteur1 = new Capteur("1", "Température", "Paris");
        Capteur capteur2 = new Capteur("2", "Température", "Lyon");
        Capteur capteur3 = new Capteur("3", "Humidité", "Marseille");

        List<Capteur> capteurs = Arrays.asList(capteur1, capteur2, capteur3);
        Map<String, Capteur> capteursMap = capteurs.stream().collect(Collectors.toMap(Capteur::id, c -> c));

        List<Mesure> mesures = Arrays.asList(
                new Mesure("1", 35.0, LocalDateTime.now()),  // > 30.0 => retenue
                new Mesure("1", 8.0, LocalDateTime.now()),   // < 10.0 => retenue
                new Mesure("1", 20.0, LocalDateTime.now()),  // entre 10 et 30 => rejetée
                new Mesure("2", 9.5, LocalDateTime.now()),   // < 10.0 => retenue
                new Mesure("2", 30.5, LocalDateTime.now()),  // > 30.0 => retenue
                new Mesure("3", 50.0, LocalDateTime.now())   // Capteur de type "Humidité" => rejetée
        );

        List<Mesure> filtered = FiltreMesure.filtrerParType(mesures.stream(), capteursMap, "Température", 30.0, 10.0);

        assertEquals(4, filtered.size());
        assertTrue(filtered.contains(mesures.get(0)));
        assertTrue(filtered.contains(mesures.get(1)));
        assertTrue(filtered.contains(mesures.get(3)));
        assertTrue(filtered.contains(mesures.get(4)));
        assertFalse(filtered.contains(mesures.get(2)));
        assertFalse(filtered.contains(mesures.get(5)));
    }

    @Test
    void testFiltrerHumidite() {
        Capteur capteur1 = new Capteur("1", "Humidité", "Paris");
        Capteur capteur2 = new Capteur("2", "Humidité", "Lyon");
        Capteur capteur3 = new Capteur("3", "Température", "Marseille");

        List<Capteur> capteurs = Arrays.asList(capteur1, capteur2, capteur3);
        Map<String, Capteur> capteursMap = capteurs.stream().collect(Collectors.toMap(Capteur::id, c -> c));

        List<Mesure> mesures = Arrays.asList(
                new Mesure("1", 85.0, LocalDateTime.now()),
                new Mesure("1", 15.0, LocalDateTime.now()),
                new Mesure("1", 50.0, LocalDateTime.now()),
                new Mesure("2", 90.0, LocalDateTime.now()),
                new Mesure("3", 10.0, LocalDateTime.now())
        );

        List<Mesure> filtered = FiltreMesure.filtrerParType(mesures.stream(), capteursMap, "Humidité", 80.0, 20.0);

        assertEquals(3, filtered.size());
        assertTrue(filtered.contains(mesures.get(0)));
        assertTrue(filtered.contains(mesures.get(1)));
        assertTrue(filtered.contains(mesures.get(3)));
        assertFalse(filtered.contains(mesures.get(2)));
        assertFalse(filtered.contains(mesures.get(4)));
    }

    @Test
    void testFiltrerPression() {
        Capteur capteur1 = new Capteur("1", "Pression", "Paris");
        Capteur capteur2 = new Capteur("2", "Pression", "Lyon");
        Capteur capteur3 = new Capteur("3", "Température", "Marseille");

        List<Capteur> capteurs = Arrays.asList(capteur1, capteur2, capteur3);
        Map<String, Capteur> capteursMap = capteurs.stream().collect(Collectors.toMap(Capteur::id, c -> c));

        List<Mesure> mesures = Arrays.asList(
                new Mesure("1", 110.0, LocalDateTime.now()),
                new Mesure("1", 90.0, LocalDateTime.now()),
                new Mesure("1", 100.0, LocalDateTime.now()),
                new Mesure("2", 106.0, LocalDateTime.now()),
                new Mesure("3", 80.0, LocalDateTime.now())
        );

        List<Mesure> filtered = FiltreMesure.filtrerParType(mesures.stream(), capteursMap, "Pression", 105.0, 95.0);

        assertEquals(3, filtered.size());
        assertTrue(filtered.contains(mesures.get(0)));
        assertTrue(filtered.contains(mesures.get(1)));
        assertTrue(filtered.contains(mesures.get(3)));
        assertFalse(filtered.contains(mesures.get(2)));
        assertFalse(filtered.contains(mesures.get(4)));
    }
}
