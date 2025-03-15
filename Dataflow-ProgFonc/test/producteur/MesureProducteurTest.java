package producteur;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import source.Capteur;
import source.Mesure;
import utils.Producteur;

import java.util.Arrays;
import java.util.List;

class MesureProducteurTest {

    private List<Capteur> capteurs;

    @BeforeEach
    void setUp() {
        capteurs = Arrays.asList(
                new Capteur("CAP001", "Température", "Bâtiment A"),
                new Capteur("CAP002", "Humidité", "Bâtiment B"),
                new Capteur("CAP003", "Pression", "Serre Extérieure")
        );
    }

    @Test
    void testProduireMesures_GeneratesCorrectNumberOfMeasures() {
        int nombreDeMesures = 5;
        Producteur<Mesure> producteur = MesureProducteur.creerProducteur(capteurs);
        List<Mesure> mesures = producteur.produire(nombreDeMesures);

        Assertions.assertEquals(nombreDeMesures, mesures.size(),
                "Le nombre de mesures générées doit correspondre à la valeur demandée.");
    }

    @Test
    void testProduireMesures_GeneratesMeasuresWithValidCapteur() {
        int nombreDeMesures = 10;
        Producteur<Mesure> producteur = MesureProducteur.creerProducteur(capteurs);
        List<Mesure> mesures = producteur.produire(nombreDeMesures);

        for (Mesure mesure : mesures) {
            boolean capteurValide = capteurs.stream().anyMatch(capteur -> capteur.id().equals(mesure.capteurId()));
            Assertions.assertTrue(capteurValide, "Le capteur de la mesure " + mesure.capteurId() + " n'est pas valide.");
        }
    }

    @Test
    void testProduireMesures_GeneratesMeasuresWithValidValues() {
        int nombreDeMesures = 5;
        Producteur<Mesure> producteur = MesureProducteur.creerProducteur(capteurs);
        List<Mesure> mesures = producteur.produire(nombreDeMesures);

        for (Mesure mesure : mesures) {
            Assertions.assertTrue(mesure.valeur() >= 0 && mesure.valeur() < 100,
                    "La valeur de la mesure doit être comprise entre 0 et 100, mais était " + mesure.valeur());
        }
    }

    @Test
    void testProduireMesures_WithZeroMeasures() {
        Producteur<Mesure> producteur = MesureProducteur.creerProducteur(capteurs);
        List<Mesure> mesures = producteur.produire(0);

        Assertions.assertTrue(mesures.isEmpty(),
                "La liste des mesures devrait être vide lorsque le nombre demandé est 0.");
    }
}
