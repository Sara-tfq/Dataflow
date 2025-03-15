package utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class Producteur<T> {
    private final Supplier<T> generateur;

    public Producteur(Supplier<T> generateur) {
        this.generateur = generateur;
    }

    public List<T> produire(int nombre) {
        return IntStream.range(0, nombre)
                .parallel()
                .mapToObj(i -> generateur.get())
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }
}
