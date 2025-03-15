package producteur;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import source.Capteur;
import source.Mesure;
import utils.Producteur;

import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

class CapteurProducteurTest {

    @Test
    void testProduireCapteurs_Taille() {
        int nombre = 10;
        Producteur<Capteur> producteur = CapteurProducteur.creerProducteur();
        List<Capteur> capteurs = producteur.produire(nombre);

        Assertions.assertNotNull(capteurs, "La liste des capteurs ne doit pas être nulle");
        Assertions.assertEquals(nombre, capteurs.size(), "Le nombre de capteurs généré doit être égal à " + nombre);
    }

    @Test
    void testProduireCapteurs_AttributsNonNull() {
        int nombre = 5;
        Producteur<Capteur> producteur = CapteurProducteur.creerProducteur();
        List<Capteur> capteurs = producteur.produire(nombre);

        capteurs.forEach(capteur -> {
            Assertions.assertNotNull(capteur.id(), "L'identifiant du capteur ne doit pas être nul");
            Assertions.assertNotNull(capteur.type(), "Le type du capteur ne doit pas être nul");
            Assertions.assertNotNull(capteur.localisation(), "La localisation du capteur ne doit pas être nulle");
        });
    }

    @Test
    void testProduireCapteurs_TypesEtLocalisationsValides() {
        int nombre = 15;
        Producteur<Capteur> producteur = CapteurProducteur.creerProducteur();
        List<Capteur> capteurs = producteur.produire(nombre);

        Set<String> typesAttendus = Set.of("Température", "Humidité", "Pression", "CO2");
        Set<String> localisationsAttendus = Set.of("Bâtiment A", "Bâtiment B", "Serre Extérieure");

        capteurs.forEach(capteur -> {
            Assertions.assertTrue(typesAttendus.contains(capteur.type()),
                    "Le type du capteur (" + capteur.type() + ") n'est pas valide");
            Assertions.assertTrue(localisationsAttendus.contains(capteur.localisation()),
                    "La localisation du capteur (" + capteur.localisation() + ") n'est pas valide");
        });
    }

    @Test
    void testProduireCapteurs_UniqueId() {
        int nombre = 20;
        Producteur<Capteur> producteur = CapteurProducteur.creerProducteur();
        List<Capteur> capteurs = producteur.produire(nombre);

        List<String> ids = capteurs.stream()
                .map(Capteur::id)
                .collect(Collectors.toList());
        long distinctCount = ids.stream().distinct().count();
        Assertions.assertEquals(nombre, distinctCount, "Chaque capteur doit avoir un identifiant unique");
    }
}

