package producteur;

import source.Mesure;
import source.Capteur;
import utils.Producteur;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MesureProducteur {
    public static Producteur<Mesure> creerProducteur(List<Capteur> capteurs) {
        return new Producteur<>(() -> {
            int index = ThreadLocalRandom.current().nextInt(capteurs.size());
            return new Mesure(capteurs.get(index).id(), ThreadLocalRandom.current().nextDouble(0, 100));
        });
    }
}
