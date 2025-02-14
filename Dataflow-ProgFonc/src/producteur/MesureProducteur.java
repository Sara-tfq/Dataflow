package producteur;

import source.Mesure;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class MesureProducteur {

    private static final String[] CAPTEURS = {"CAP001", "CAP002", "CAP003", "CAP004", "CAP005"};

    /**
     * Génère une mesure aléatoire.
     *
     * @return une instance de Mesure avec un capteur aléatoire, une valeur aléatoire, et un timestamp courant.
     */
    public Mesure produireMesure() {
        // Sélection d'un identifiant de capteur aléatoire
        int index = ThreadLocalRandom.current().nextInt(CAPTEURS.length);
        String capteurId = CAPTEURS[index];

        // Génération d'une valeur aléatoire, par exemple entre 0 et 100
        double valeur = ThreadLocalRandom.current().nextDouble(0, 100);

        // Utilisation du constructeur secondaire qui ajoute le timestamp automatiquement
        return new Mesure(capteurId, valeur);
    }

    public static void main(String[] args) throws InterruptedException {
        MesureProducteur producteur = new MesureProducteur();
        // Génération et affichage de 10 mesures aléatoires
        for (int i = 0; i < 10; i++) {
            Mesure mesure = producteur.produireMesure();
            System.out.println(mesure);
            Thread.sleep(1000); // Pause d'une seconde pour simuler un flux continu
        }
    }
}

