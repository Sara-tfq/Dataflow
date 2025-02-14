package source;

import java.util.UUID;

public record Capteur(String id, String type, String localisation) {
    public static Capteur generate(String type, String localisation) {
        return new Capteur(UUID.randomUUID().toString(), type, localisation);
    }
}


