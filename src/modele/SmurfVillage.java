package modele;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import modele.ShopManager;

public class SmurfVillage {

    private final Map<Resource, Integer> resources;
    private final List<Character> characters;
    private final List<Event> eventHistory;
    private final List<CrisisType> currentCrises;
    private final Random random;

    public SmurfVillage() {
        resources = new HashMap<Resource, Integer>();
        characters = new ArrayList<Character>();
        eventHistory = new ArrayList<Event>();
        currentCrises = new ArrayList<CrisisType>();
        random = new Random();

        initializeResources();
        initializeCharacters();
    }

    private void initializeResources() {
        for (Resource resource : Resource.values()) {
            resources.put(resource, 5);
        }
    }

    private void initializeCharacters() {
        // Schtroumpfs gratuits de base
        characters.add(new PapaSmurf());
        characters.add(new HandySmurf());
        characters.add(new GreedySmurf());
        characters.add(new Smurfette());
        characters.add(new JokeySmurf());

        // Schtroumpfs premium — ajoutés seulement si achetés
        if (ShopManager.isPurchased("BRAINY"))
            characters.add(new BrainySmurf());
        if (ShopManager.isPurchased("MINER"))
            characters.add(new MinerSmurf());
        if (ShopManager.isPurchased("MAGICIAN"))
            characters.add(new MagicianSmurf());
    }

    // Recharge les personnages après un achat en boutique.
    public void refreshCharacters() {
        characters.clear();
        initializeCharacters();
    }

    public int getQuantity(Resource resource) {
        Objects.requireNonNull(resource, "Resource cannot be null");
        return resources.get(resource);
    }

    public void modifyResource(Resource resource, int variation) {
        Objects.requireNonNull(resource, "Resource cannot be null");

        int oldValue = resources.get(resource);
        int newValue = oldValue + variation;

        if (newValue < 0) newValue = 0;
        if (newValue > 10) newValue = 10;

        resources.put(resource, newValue);
    }

    public boolean hasEnoughResource(Resource resource, int requiredQuantity) {
        Objects.requireNonNull(resource, "Resource cannot be null");
        return getQuantity(resource) >= requiredQuantity;
    }

    public String produceResources() {
        int berriesProduction      = random.nextInt(3) + 1;
        int sarsaparillaProduction = random.nextInt(2) + 1;
        int moraleProduction       = random.nextInt(2);
        int defenseProduction      = random.nextInt(2);
        int knowledgeProduction    = random.nextInt(2) + 1;

        if (currentCrises.contains(CrisisType.FAMINE))
            berriesProduction = 0;
        if (currentCrises.contains(CrisisType.EPIDEMIC)) {
            sarsaparillaProduction = 0;
            moraleProduction = 0;
        }
        if (currentCrises.contains(CrisisType.SMURF_REVOLT))
            moraleProduction = 0;
        if (currentCrises.contains(CrisisType.MASSIVE_ATTACK))
            defenseProduction = 0;
        if (currentCrises.contains(CrisisType.RECIPE_FORGETTING))
            knowledgeProduction = 0;

        modifyResource(Resource.BERRIES,      berriesProduction);
        modifyResource(Resource.SARSAPARILLA, sarsaparillaProduction);
        modifyResource(Resource.MORALE,       moraleProduction);
        modifyResource(Resource.DEFENSE,      defenseProduction);
        modifyResource(Resource.KNOWLEDGE,    knowledgeProduction);

        return "Le village a produit :\n\n"
                + "+ " + berriesProduction      + " baies\n"
                + "+ " + sarsaparillaProduction + " salsepareille\n"
                + "+ " + moraleProduction       + " moral\n"
                + "+ " + defenseProduction      + " défense\n"
                + "+ " + knowledgeProduction    + " savoir";
    }

    public void consumeResources() {
        modifyResource(Resource.BERRIES,      -2);
        modifyResource(Resource.SARSAPARILLA, -2);
    }

    public void updateCrises() {
        currentCrises.clear();

        if (getQuantity(Resource.BERRIES)      == 0) currentCrises.add(CrisisType.FAMINE);
        if (getQuantity(Resource.SARSAPARILLA) == 0) currentCrises.add(CrisisType.EPIDEMIC);
        if (getQuantity(Resource.MORALE)       == 0) currentCrises.add(CrisisType.SMURF_REVOLT);
        if (getQuantity(Resource.DEFENSE)      == 0) currentCrises.add(CrisisType.MASSIVE_ATTACK);
        if (getQuantity(Resource.KNOWLEDGE)    == 0) currentCrises.add(CrisisType.RECIPE_FORGETTING);
    }

    public void applyCrisisEffects() {
        for (CrisisType crisis : currentCrises) {
            switch (crisis) {
                case FAMINE          -> modifyResource(Resource.MORALE, -1);
                case EPIDEMIC        -> { modifyResource(Resource.MORALE, -1);
                                         modifyResource(Resource.KNOWLEDGE, -1); }
                case SMURF_REVOLT    -> { modifyResource(Resource.DEFENSE, -1);
                                         modifyResource(Resource.KNOWLEDGE, -1); }
                case MASSIVE_ATTACK  -> { modifyResource(Resource.BERRIES, -1);
                                         modifyResource(Resource.DEFENSE, -1); }
                case RECIPE_FORGETTING -> { modifyResource(Resource.BERRIES, -2);
                                            modifyResource(Resource.SARSAPARILLA, -1); }
            }
        }
    }

    public List<CrisisType> getCurrentCrises() { return currentCrises; }

    public String getCrisesDescription() {
        if (currentCrises.isEmpty()) return "Aucune crise active.";
        String text = "Crises actives :\n";
        for (CrisisType crisis : currentCrises) text += "• " + crisis + "\n";
        return text;
    }

    public void addEventToHistory(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        eventHistory.add(event);
    }

    public List<Event>     getEventHistory() { return eventHistory; }
    public List<Character> getCharacters()   { return characters; }

    public int calculateScore() {
        int score = 0;
        for (Resource resource : Resource.values()) score += getQuantity(resource);
        score += (12 - currentCrises.size()) * 5;
        return score;
    }

    public boolean isVictory() { return currentCrises.size() < 3; }

    public String getCrisisPenaltyDescription() {
        if (currentCrises.isEmpty()) return "Aucune crise active.";

        String text = "Attention ! Crise active :\n\n";
        for (CrisisType crisis : currentCrises) {
            text += "• " + crisis + "\n";
            switch (crisis) {
                case FAMINE            -> text += "Pénalité : le moral baisse et la production de baies est bloquée.\n\n";
                case EPIDEMIC          -> text += "Pénalité : le moral baisse et le savoir diminue.\n\n";
                case SMURF_REVOLT      -> text += "Pénalité : la défense et le savoir diminuent.\n\n";
                case MASSIVE_ATTACK    -> text += "Pénalité : les baies et la défense diminuent.\n\n";
                case RECIPE_FORGETTING -> text += "Pénalité : les baies et la salsepareille diminuent.\n\n";
            }
        }
        return text;
    }
}
