package model;

import module java.base;

public class VillageSchtroumpf {

    private final String name;
    private final Map<Ressource, Integer> resources = new EnumMap<>(Ressource.class);
    private final List<Personnage> characters = new ArrayList<>();
    private final List<Evenement> history = new ArrayList<>();
    private int turn = 1;

    public VillageSchtroumpf(String name) {
        this.name = name;
        initResources();
        initCharacters();
    }

    private void initResources() {
        for (Ressource r : Ressource.values()) {
            resources.put(r, 5);
        }
    }

    private void initCharacters() {
        // À remplir plus tard
    }

    public String getName() {
        return name;
    }

    public int getResource(Ressource r) {
        return resources.get(r);
    }

    public int getTurn() {
        return turn;
    }

    public List<Evenement> getHistory() {
        return history;
    }

    public List<Personnage> getCharacters() {
        return characters;
    }


    // Modification des ressources   
    public void addResource(Ressource r, int amount) {
        int newValue = limitValue(resources.get(r) + amount);
        resources.put(r, newValue);
    }

    private int limitValue(int value) {
        return Math.max(0, Math.min(10, value));
    }


    // Gestion des tours
    public void nextTurn() {
        turn++;
    }


    // Score final
    public double computeFinalScore() {
        int score = 0;

        score += getResource(Ressource.BAIES) * 1.25;
        score += getResource(Ressource.SALSEPAREILLE);
        score += getResource(Ressource.OR)* 4;
        score += getResource(Ressource.OUTILS);
        score += getResource(Ressource.MORAL) * 3;
        score += getResource(Ressource.DEFENSE) * 2;
        score += getResource(Ressource.SAVOIR) * 1.5;

        return score * 10;
    }
}
