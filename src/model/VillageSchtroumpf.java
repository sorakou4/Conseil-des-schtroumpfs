package model;

import module java.base;

public class VillageSchtroumpf {

    private final String name;
    private final Map<Ressource, Integer> resources = new EnumMap<>(Ressource.class);
    private final List<Personnage> characters = new ArrayList<>();
    private final List<Evenement> history = new ArrayList<>();
    private int turn = 1;

    // Initialise le village avec un nom, des ressources et des personnages
    public VillageSchtroumpf(String name) {
        this.name = name;
        initResources();
        initCharacters();
    }

    // Initialise toutes les ressources à 5 au début de la partie
    private void initResources() {
        for (Ressource r : Ressource.values()) {
            resources.put(r, 5);
        }
    }

    // Ajoute les personnages de départ
    private void initCharacters() {
    	characters.add(new GrandSchtroumpf());
    	// characters.add(new SchtroumpfBricoleur());
        // characters.add(new SchtroumpfGourmand());
        // characters.add(new Schtroumpfette());
    }

    // Retourne le nom du village
    public String getName() {
        return name;
    }

    // Retourne la valeur d'une ressource donnée
    public int getResource(Ressource r) {
        return resources.get(r);
    }

    // Retourne le numéro du tour actuel
    public int getTurn() {
        return turn;
    }

    // Retourne l'historique des événements
    public List<Evenement> getHistory() {
        return history;
    }

    // Retourne la liste des personnages
    public List<Personnage> getCharacters() {
        return characters;
    }


    // --- Modification des ressources ---

    // Ajoute (ou retire) une quantité à une ressource, en respectant la limite 0–10
    public void addResource(Ressource r, int amount) {
        int newValue = limitValue(resources.get(r) + amount);
        resources.put(r, newValue);
    }

    // Empêche une ressource de dépasser les bornes (0 minimum, 10 maximum)
    private int limitValue(int value) {
        return Math.max(0, Math.min(10, value));
    }


    // --- Gestion des tours ---

    // Passe au tour suivant
    public void nextTurn() {
        turn++;
    }


    // --- Calcul du score final ---

    public double computeFinalScore() {
        int score = 0;

        // Poids (valeur) des ressources selon leur importance
        score += getResource(Ressource.BAIES) * 1.25;
        score += getResource(Ressource.SALSEPAREILLE);
        score += getResource(Ressource.OR) * 4;
        score += getResource(Ressource.OUTILS);
        score += getResource(Ressource.MORAL) * 3;
        score += getResource(Ressource.DEFENSE) * 2;
        score += getResource(Ressource.SAVOIR) * 1.5;

        return score * 10;
    }
}
