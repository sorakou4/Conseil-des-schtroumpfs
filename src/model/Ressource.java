package model;

// Enum représentant les différents types de ressources du village.
public enum Ressource {
    BAIES,
    SALSEPAREILLE,
    OUTILS,
    DEFENSE,
    MORAL,
    SAVOIR,
    OR;

	// Classe interne représentant la valeur d'une ressource (limite 0-10)
	public static class Value {

	    // Quantité actuelle de la ressource
	    private int amount;

	    // initialise la ressource en appliquant la limite
	    public Value(int initial) {
	        this.amount = limitValue(initial);
	    }

	    // Retourne la valeur actuelle
	    public int get() {
	        return amount;
	    }

	    // Définit une nouvelle valeur + limite
	    public void set(int value) {
	        this.amount = limitValue(value);
	    }

	    // Variation de la ressource (ajout/suppression) + limite
	    public void add(int variation) {
	        this.amount = limitValue(this.amount + variation);
	    }

	    // Empêche la valeur de dépasser les bornes (0 minimum et 10 maximum)
	    private int limitValue(int value) {
	        if (value < 0) return 0;
	        if (value > 10) return 10;
	        return value;
	    }
	}
}
