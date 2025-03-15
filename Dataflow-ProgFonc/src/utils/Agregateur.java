package utils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Agregateur<T, K, A> {

    public Map<K, A> aggreger(Stream<T> stream, Function<T, K> classifier, Collector<T, ?, A> collector) {
        return stream.collect(Collectors.groupingBy(classifier, collector));
    }
}
