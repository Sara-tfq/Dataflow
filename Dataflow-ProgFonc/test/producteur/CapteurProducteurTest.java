package producteur;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import source.Capteur;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class CapteurProducteurTest {

    @Test
    public void testGenererCapteurs_Taille() {
        int nombre = 10;
        List<Capteur> capteurs = CapteurProducteur.genererCapteurs(nombre);
        System.out.println("Liste des capteurs : " +  capteurs);
        Assertions.assertNotNull(capteurs, "La liste des capteurs ne doit pas être nulle");
        Assertions.assertEquals(nombre, capteurs.size(), "Le nombre de capteurs généré doit être égal à " + nombre);
    }

    @Test
    public void testGenererCapteurs_AttributsNonNull() {
        int nombre = 5;
        List<Capteur> capteurs = CapteurProducteur.genererCapteurs(nombre);

        capteurs.forEach(capteur -> {
            Assertions.assertNotNull(capteur.id(), "L'identifiant du capteur ne doit pas être nul");
            Assertions.assertNotNull(capteur.type(), "Le type du capteur ne doit pas être nul");
            Assertions.assertNotNull(capteur.localisation(), "La localisation du capteur ne doit pas être nulle");
        });
    }

    @Test
    public void testGenererCapteurs_TypesEtLocalisationsValides() {
        int nombre = 15;
        List<Capteur> capteurs = CapteurProducteur.genererCapteurs(nombre);

        // Liste des types et localisations attendus
        Set<String> typesAttendus = Set.of("Température", "Humidité", "Pression");
        Set<String> localisationsAttendus = Set.of("Paris", "Lyon", "Marseille");

        // Vérifie que chaque capteur a un type et une localisation valide
        capteurs.forEach(capteur -> {
            Assertions.assertTrue(typesAttendus.contains(capteur.type()),
                    "Le type du capteur (" + capteur.type() + ") n'est pas valide");
            Assertions.assertTrue(localisationsAttendus.contains(capteur.localisation()),
                    "La localisation du capteur (" + capteur.localisation() + ") n'est pas valide");
        });
    }

    @Test
    public void testGenererCapteurs_UniqueId() {
        int nombre = 20;
        List<Capteur> capteurs = CapteurProducteur.genererCapteurs(nombre);

        // Récupère les identifiants et s'assure qu'il n'y a pas de doublons
        List<String> ids = capteurs.stream()
                .map(Capteur::id)
                .collect(Collectors.toList());
        long distinctCount = ids.stream().distinct().count();
        Assertions.assertEquals(nombre, distinctCount, "Chaque capteur doit avoir un identifiant unique");
    }

}