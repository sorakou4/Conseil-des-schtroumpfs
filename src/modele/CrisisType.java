package modele;

public enum CrisisType {

    FAMINE("Famine"),
    EPIDEMIC("Épidémie"),
    SMURF_REVOLT("Révolte des Schtroumpfs"),
    MASSIVE_ATTACK("Attaque massive"),
    RECIPE_FORGETTING("Oubli des recettes");

    private String displayName;

    CrisisType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}