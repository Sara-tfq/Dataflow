package workerMesure;

import source.Mesure;

import java.util.function.Function;

public class TransformMesure {

    public static Mesure transformer(Mesure mesure, Function<Double, Double> conversionFunction) {
        double nouvelleValeur = conversionFunction.apply(mesure.valeur());
        return new Mesure(mesure.capteurId(), nouvelleValeur, mesure.timestamp());
    }


    public static Mesure celsiusToFahrenheit(Mesure mesure) {
        return transformer(mesure, celsius -> celsius * 9.0 / 5.0 + 32);
    }


    public static Mesure pascalToBar(Mesure mesure) {
        return transformer(mesure, pascal -> pascal / 100000);
    }





}
