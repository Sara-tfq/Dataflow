package producteur;

import source.Capteur;
import source.Mesure;
import utils.Producteur;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MesureProducteur {

    private static double genererValeurPourType(String type) {
        switch (type) {
            case "Température":
                return ThreadLocalRandom.current().nextDouble(20, 30);
            case "Humidité":
                return ThreadLocalRandom.current().nextDouble(40, 70);
            case "CO2":
                return ThreadLocalRandom.current().nextDouble(600, 900);
            case "Pression":
                return ThreadLocalRandom.current().nextDouble(101000, 102000);
            default:
                return ThreadLocalRandom.current().nextDouble(0, 100);
        }
    }

    public static Producteur<Mesure> creerProducteur(List<Capteur> capteurs) {
        return new Producteur<>(() -> {
            int index = ThreadLocalRandom.current().nextInt(capteurs.size());
            Capteur capteur = capteurs.get(index);
            double valeur = genererValeurPourType(capteur.type());
            return new Mesure(capteur.id(), valeur);
        });
    }
}
