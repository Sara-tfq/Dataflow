package utils;

import java.util.function.Function;

public class Transformateur<T> {
    public T transformer(T valeur, Function<T, T> transformation) {
        return transformation.apply(valeur);
    }
}
