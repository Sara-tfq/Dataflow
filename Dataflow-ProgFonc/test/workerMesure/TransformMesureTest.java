package workerMesure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import source.Mesure;

import java.time.LocalDateTime;

public class TransformMesureTest {

    @Test
    void testCelsiusToFahrenheit() {
        LocalDateTime timestamp = LocalDateTime.now();
        Mesure mesureCelsius = new Mesure("CAP001", 25.0, timestamp);

        Mesure mesureFahrenheit = TransformMesure.celsiusToFahrenheit(mesureCelsius);
        double expectedFahrenheit = 25.0 * 9.0 / 5.0 + 32.0;

        assertEquals(expectedFahrenheit, mesureFahrenheit.valeur(), 0.0001, "25°C doit être converti en 77°F");
        assertEquals("CAP001", mesureFahrenheit.capteurId(), "L'id du capteur doit rester le même");
        assertEquals(timestamp, mesureFahrenheit.timestamp(), "Le timestamp doit rester le même");
    }

    @Test
    void testPascalToBar() {
        LocalDateTime timestamp = LocalDateTime.now();
        Mesure mesurePascal = new Mesure("CAP002", 101325.0, timestamp);

        Mesure mesureBar = TransformMesure.pascalToBar(mesurePascal);
        double expectedBar = 101325.0 / 100000.0;

        assertEquals(expectedBar, mesureBar.valeur(), 0.0001, "101325 Pa doit être converti en environ 1.01325 Bar");
        assertEquals("CAP002", mesureBar.capteurId(), "L'id du capteur doit rester le même");
        assertEquals(timestamp, mesureBar.timestamp(), "Le timestamp doit rester le même");
    }
}
