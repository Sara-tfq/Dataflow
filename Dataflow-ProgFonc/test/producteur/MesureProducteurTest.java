package producteur;

import org.junit.jupiter.api.Test;
import source.Mesure;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MesureProducteurTest {

    @Test
    void testProduireMesure_NotNull() {
        MesureProducteur producteur = new MesureProducteur();
        Mesure mesure = producteur.produireMesure();
        System.out.println("Mesure générée : " + mesure);
        assertNotNull(mesure, "La mesure ne doit pas être nulle");
    }

    @Test
    void testProduireMesure_Attributs() {
        MesureProducteur producteur = new MesureProducteur();
        Mesure mesure = producteur.produireMesure();
        System.out.println("Mesure générée : " + mesure);

        // Vérification de l'ID du capteur
        assertNotNull(mesure.capteurId(), "L'identifiant du capteur ne doit pas être nul");

        // Vérification de la valeur (par exemple, elle doit être comprise entre 0 et 100)
        assertTrue(mesure.valeur() >= 0 && mesure.valeur() <= 100,
                "La valeur de la mesure doit être comprise entre 0 et 100");

        // Vérification du timestamp
        assertNotNull(mesure.timestamp(), "Le timestamp ne doit pas être nul");
    }

    @Test
    void testProduireMesures_FluxContinu() {
        MesureProducteur producteur = new MesureProducteur();
        int nombreMesures = 10;

        // Génération d'une liste de mesures
        List<Mesure> mesures = IntStream.range(0, nombreMesures)
                .mapToObj(i -> producteur.produireMesure())
                .toList();

        // Affichage de chaque mesure pour vérification
        mesures.forEach(m -> System.out.println("Mesure : " + m));

        assertEquals(nombreMesures, mesures.size(),
                "Le nombre de mesures générées doit être égal à " + nombreMesures);
    }
}
