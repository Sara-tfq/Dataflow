package workerMesure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import source.Mesure;
import workerMesure.TransformMesure;

import java.time.LocalDateTime;

public class TransformMesureTest {

    @Test
    void testCelsiusToFahrenheit() {
        Mesure mesureCelsius = new Mesure("CAP001", 25.0, LocalDateTime.now());
        Mesure mesureFahrenheit = TransformMesure.celsiusToFahrenheit(mesureCelsius);
        double expectedFahrenheit = 77.0;
        assertEquals(expectedFahrenheit, mesureFahrenheit.valeur(), 0.0001, "25°C doit être converti en 77°F");
        assertEquals(mesureCelsius.capteurId(), mesureFahrenheit.capteurId(), "L'id du capteur doit rester le même");
        assertEquals(mesureCelsius.timestamp(), mesureFahrenheit.timestamp(), "Le timestamp doit rester le même");
    }

    @Test
    void testPascalToBar() {
        Mesure mesurePascal = new Mesure("CAP002", 101325.0, LocalDateTime.now());
        Mesure mesureBar = TransformMesure.pascalToBar(mesurePascal);
        double expectedBar = 1.01325;
        assertEquals(expectedBar, mesureBar.valeur(), 0.0001, "101325 Pa doit être converti en environ 1.01325 Bar");
        assertEquals(mesurePascal.capteurId(), mesureBar.capteurId(), "L'id du capteur doit rester le même");
        assertEquals(mesurePascal.timestamp(), mesureBar.timestamp(), "Le timestamp doit rester le même");
    }

    @Test
    void testGenericTransformer() {
        Mesure original = new Mesure("CAP003", 10.0, LocalDateTime.now());
        Mesure transformed = TransformMesure.transformer(original, value -> value * 2);
        double expectedValue = 20.0;
        assertEquals(expectedValue, transformed.valeur(), 0.0001, "La valeur transformée doit être le double de la valeur originale");
        assertEquals(original.capteurId(), transformed.capteurId(), "L'id du capteur doit rester le même");
        assertEquals(original.timestamp(), transformed.timestamp(), "Le timestamp doit rester le même");
    }
}
