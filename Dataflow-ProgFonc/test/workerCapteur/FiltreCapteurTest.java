package workerCapteur;

import org.junit.jupiter.api.Test;
import source.Capteur;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiltreCapteurTest {

    @Test
    void testFiltrerParType() {
        Capteur capteur1 = new Capteur("1", "Température", "Paris");
        Capteur capteur2 = new Capteur("2", "Humidité", "Nice");
        Capteur capteur3 = new Capteur("3","Température", "Toulouse");

        Stream<Capteur> capteurs = Stream.of(capteur1, capteur2, capteur3);
        List<Capteur> result = FiltreCapteur.filtrerParType(capteurs, "Température").toList();

        assertEquals(2, result.size());
        assertTrue(result.contains(capteur1));
        assertTrue(result.contains(capteur3));
    }

    @Test
    void testFiltrerParLocalisation() {
        Capteur capteur1 = new Capteur("1", "Humidité", "Nice");
        Capteur capteur2 = new Capteur("2", "Humidité", "Valbonne");
        Capteur capteur3 = new Capteur("3","Température", "Nice");

        Stream<Capteur> capteurs = Stream.of(capteur1, capteur2, capteur3);
        List<Capteur> result = FiltreCapteur.filtrerParLocalisation(capteurs, "Nice").toList();

        assertEquals(2, result.size());
        assertTrue(result.contains(capteur1));
        assertTrue(result.contains(capteur3));
    }

    @Test
    void testFiltrerParTypeEtLocalisation() {
        Capteur capteur1 = new Capteur("1", "Pression", "Valbonne");
        Capteur capteur2 = new Capteur("2", "Température", "Antibes");
        Capteur capteur3 = new Capteur("3", "Pression", "Valbonne");

        Stream<Capteur> capteurs = Stream.of(capteur1, capteur2, capteur3);
        List<Capteur> result = FiltreCapteur.filtrerParTypeEtLocalisation(capteurs, "Pression", "Valbonne").toList();

        assertEquals(2, result.size());
        assertTrue(result.contains(capteur1));
        assertTrue(result.contains(capteur3));
    }
}