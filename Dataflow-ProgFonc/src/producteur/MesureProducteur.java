package producteur;

import source.Capteur;
import source.Mesure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MesureProducteur {

    private final List<Capteur> capteurs;

    // Ajout d'un constructeur qui reçoit la liste des capteurs
    public MesureProducteur(List<Capteur> capteurs) {
        this.capteurs = capteurs;
    }

    /**
     * Génère un ensemble de mesures aléatoires.
     *
     * @param nombre le nombre de mesures à générer
     * @return une liste de Mesure avec un capteur aléatoire (parmi ceux fournis),
     * une valeur aléatoire, et un timestamp courant.
     */
    public List<Mesure> produireMesures(int nombre) {
        List<Mesure> mesures = new ArrayList<>();
        for (int i = 0; i < nombre; i++) {
            // Sélection d'un capteur aléatoire dans la liste fournie
            int index = ThreadLocalRandom.current().nextInt(capteurs.size());
            String capteurId = capteurs.get(index).id();
            // Génération d'une valeur aléatoire (par exemple entre 0 et 100)
            double valeur = ThreadLocalRandom.current().nextDouble(0, 100);
            mesures.add(new Mesure(capteurId, valeur));
        }
        return mesures;
    }
}
