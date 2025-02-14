package source;

import java.time.LocalDateTime;

public record Mesure(String capteurId, double valeur, LocalDateTime timestamp) {
    public Mesure(String capteurId, double valeur) {
        this(capteurId, valeur, LocalDateTime.now());
    }
}
