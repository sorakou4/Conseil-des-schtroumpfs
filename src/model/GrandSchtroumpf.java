package model;

public class GrandSchtroumpf implements Personnage {

    @Override
    public String getName() {
        return "Grand Schtroumpf";
    }

    // Calcule l'efficacité du personnage selon la phase du jour et l'événement actif
    @Override
    public double getEfficiency(Phase phase, Evenement event) {
        double efficiency = 1.0; // efficacité de base

        // Bonus le matin : il organise le village et motive les autres (pour le gameplay)
        if (phase == Phase.MORNING) {
            efficiency += 0.5;
        }

        // Malus si une tempête est en cours
        if (event != null && event.getName().equalsIgnoreCase("Tempête")) {
            efficiency -= 0.3;
        }

        // Bonus si c'est la fête du village
        if (event != null && event.getName().equalsIgnoreCase("Fête du village")) {
            efficiency += 0.4;
        }

        return efficiency;
    }

    @Override
    public void performAction(VillageSchtroumpf village, Phase phase, Evenement event) {

        // Exemple : choix aléatoire parmi les 3 actions
        int action = (int) (Math.random() * 3);

        switch (action) {
            case 0 -> consulterGrimoire(village, phase, event);
            case 1 -> organiserReunion(village, phase, event);
            case 2 -> negocierAvecAnimaux(village, phase, event);
        }
    }

    // ACTION 1 : Consulter le grimoire
    private void consulterGrimoire(VillageSchtroumpf village, Phase phase, Evenement event) {
        double eff = getEfficiency(phase, event);

        // Gain de savoir basé sur l’efficacité
        int gain = (int) Math.round(1 * eff);
        village.addResource(Ressource.SAVOIR, gain);

        // Risque d’échec si efficacité faible
        if (eff < 0.8) {
            village.addResource(Ressource.MORAL, -1);
        }
    }

    // ACTION 2 : Organiser une réunion
    private void organiserReunion(VillageSchtroumpf village, Phase phase, Evenement event) {
        double eff = getEfficiency(phase, event);

        // Le moral augmente selon l’efficacité
        int gain = (int) Math.round(1 * eff);
        village.addResource(Ressource.MORAL, gain);
    }

    // ACTION 3 : Négocier avec les animaux
    private void negocierAvecAnimaux(VillageSchtroumpf village, Phase phase, Evenement event) {
        double eff = getEfficiency(phase, event);

        // Choix aléatoire : Or ou Défense
        boolean or = Math.random() < 0.5;

        int gain = (int) Math.round(1 * eff);

        if (or) {
            village.addResource(Ressource.OR, gain);
        } else {
            village.addResource(Ressource.DEFENSE, gain);
        }
    }
}

