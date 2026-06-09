package modele;

import java.util.Random;

public class EventGenerator {

    // Générateur aléatoire
    private final Random random;

    public EventGenerator() {
        random = new Random();
    }

    public Event generateRandomEvent() {

        // Nombre entre 0 et 99
        int value = random.nextInt(100);

        // 20% Gargamel
        if (value < 20) {
            return new Event(EventType.GARGAMEL_ATTACK);
        }

        // 20% Baies magiques
        if (value < 40) {
            return new Event(EventType.MAGIC_BERRIES_DISCOVERY);
        }

        // 15% Village ami
        if (value < 55) {
            return new Event(EventType.FRIENDLY_VILLAGE_VISIT);
        }

        // 15% Tempête de salsepareille
        if (value < 70) {
            return new Event(EventType.SARSAPARILLA_STORM);
        }

        // 15% Festival
        if (value < 85) {
            return new Event(EventType.SMURF_FESTIVAL);
        }

        // 15% Malédiction de la forêt
        return new Event(EventType.FOREST_CURSE);
    }
}
