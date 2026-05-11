package model;

public interface Personnage {
	
	// Nom du Schtroumf  
    String getName();

    // Action principale du personnage
    void performAction(VillageSchtroumpf village, Phase phase, Evenement event);

    // Efficacité selon la phase + événement
    double getEfficiency(Phase phase, Evenement event);
}
