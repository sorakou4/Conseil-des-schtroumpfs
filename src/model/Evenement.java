package model;

public class Evenement {

    // Nom de l'événement
    private final String name;

    // Nombre de tours restants avant la fin de l'événement
    private int remainingTurns;

    // Crée un événement avec un nom et une durée en tours
    public Evenement(String name, int duration) {
        this.name = name;
        this.remainingTurns = duration;
    }

    // Retourne le nom de l'événement
    public String getName() {
        return name;
    }

    // Retourne combien de tours il reste avant la fin de l'événement
    public int getRemainingTurns() {
        return remainingTurns;
    }

    // Applique l'effet de l'événement sur le village
    public void applyEffect(VillageSchtroumpf village) {
        // Exemple : réduire les baies, baisser le moral, etc... proposer vos idées ! =)
    }

    // (Appelé à chaque tour) réduit la durée de l'événement
    public void nextTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    // Indique si l'événement est terminé
    public boolean isFinished() {
        return remainingTurns <= 0;
    }
}

