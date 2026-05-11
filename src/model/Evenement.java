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

        switch (name) {
        	
        	// --- Événement des saisons aléatoires ---
	        case "Printemps" -> {
	            village.addResource(Ressource.BAIES, +1);
	            village.addResource(Ressource.MORAL, +1);
	        }
	
	        case "Été" -> {
	            village.addResource(Ressource.BAIES, +1);
	            village.addResource(Ressource.DEFENSE, -1);
	        }
	
	        case "Automne" -> {
	            village.addResource(Ressource.SALSEPAREILLE, +1);
	            village.addResource(Ressource.BAIES, -1);
	        }
	
	        case "Hiver" -> {
	            village.addResource(Ressource.BAIES, -2);
	            village.addResource(Ressource.MORAL, -1);
	            village.addResource(Ressource.DEFENSE, +1);
	        }

        
        	// --- Événement aléatoires ---
            case "Attaque de Gargamel" -> {
                village.addResource(Ressource.DEFENSE, -2);
                village.addResource(Ressource.MORAL, -1);
            }

            case "Découverte de baies magiques" -> {
                village.addResource(Ressource.BAIES, +2);
                village.addResource(Ressource.SALSEPAREILLE, +1);
            }

            case "Visite d’un village ami" -> {
                village.addResource(Ressource.OR, +1);
                village.addResource(Ressource.MORAL, +2);
            }

            case "Tempête de Salsepareille" -> {
                village.addResource(Ressource.OUTILS, -2);
                village.addResource(Ressource.SAVOIR, +1);
            }

            case "Fête des Schtroumpfs" -> {
                village.addResource(Ressource.MORAL, +2);
                village.addResource(Ressource.BAIES, -1);
            }

            case "Malédiction de la forêt" -> {
                village.addResource(Ressource.SAVOIR, -2);
                village.addResource(Ressource.MORAL, -1);
            }
        }
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

