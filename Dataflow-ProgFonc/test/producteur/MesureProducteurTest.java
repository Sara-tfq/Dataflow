package producteur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import producteur.MesureProducteur;
import source.Capteur;
import source.Mesure;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MesureProducteurTest {

    private MesureProducteur producteur;
    private List<Capteur> capteurs;

    @BeforeEach
    void setUp() {
        // Initialisation de la liste de capteurs avec quelques exemples
        capteurs = Arrays.asList(
                new Capteur("CAP001", "Température", "Paris"),
                new Capteur("CAP002", "Humidité", "Lyon"),
                new Capteur("CAP003", "Pression", "Marseille")
        );
        producteur = new MesureProducteur(capteurs);
    }

    // Test de la génération de mesures avec un nombre correct
    @Test
    void testProduireMesures_GeneratesCorrectNumberOfMeasures() {
        int nombreDeMesures = 5;
        List<Mesure> mesures = producteur.produireMesures(nombreDeMesures);

        // Vérifier que le nombre de mesures générées est correct
        assertEquals(nombreDeMesures, mesures.size(), "Le nombre de mesures générées doit correspondre à la valeur demandée.");
    }

    // Test que chaque mesure contient un capteur valide
    @Test
    void testProduireMesures_GeneratesMeasuresWithValidCapteur() {
        int nombreDeMesures = 10;
        List<Mesure> mesures = producteur.produireMesures(nombreDeMesures);

        // Vérifier que chaque mesure a un capteur valide (présent dans la liste de capteurs)
        for (Mesure mesure : mesures) {
            boolean capteurValide = capteurs.stream().anyMatch(capteur -> capteur.id().equals(mesure.capteurId()));
            assertTrue(capteurValide, "Le capteur de la mesure " + mesure.capteurId() + " n'est pas valide.");
        }
    }

    // Test de la génération avec une liste vide de capteurs


    // Test de la génération des mesures avec des valeurs valides
    @Test
    void testProduireMesures_GeneratesMeasuresWithValidValues() {
        int nombreDeMesures = 5;
        List<Mesure> mesures = producteur.produireMesures(nombreDeMesures);

        // Vérifier que les valeurs des mesures sont comprises entre 0 et 100
        for (Mesure mesure : mesures) {
            assertTrue(mesure.valeur() >= 0 && mesure.valeur() < 100,
                    "La valeur de la mesure doit être comprise entre 0 et 100, mais la valeur était " + mesure.valeur());
        }
    }

    // Test de la génération de 0 mesures
    @Test
    void testProduireMesures_WithZeroMeasures() {
        List<Mesure> mesures = producteur.produireMesures(0);

        // Vérifier que la liste est vide lorsque le nombre de mesures est 0
        assertTrue(mesures.isEmpty(), "La liste des mesures devrait être vide lorsque le nombre demandé est 0.");
    }
}
