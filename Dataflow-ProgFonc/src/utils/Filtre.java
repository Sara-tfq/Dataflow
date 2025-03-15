package utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filtre<T> {
    public List<T> filtrer(Stream<T> stream, Predicate<T> condition) {
        return stream.parallel()
                .filter(condition)
                .collect(Collectors.toList());
    }
}
