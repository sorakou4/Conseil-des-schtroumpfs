package modele;

import java.util.Map;
import java.util.LinkedHashMap;

public enum ActionType {

    // Papa Smurf
    CONSULT_GRIMOIRE("Consulter le grimoire"),
    ORGANIZE_MEETING("Organiser une réunion"),
    NEGOTIATE_WITH_ANIMALS("Négocier avec les animaux"),

    // Handy Smurf
    REPAIR_HOUSES("Réparer les maisons"),
    BUILD_TRAP("Construire un piège"),
    INVENT_GADGET("Inventer un gadget"),

    // Greedy Smurf
    PICK_BERRIES("Cueillir des baies"),
    ORGANIZE_FEAST("Organiser un festin"),
    FIND_RARE_MUSHROOM("Chercher des champignons rares"),

    // Smurfette
    NEGOTIATE_WITH_VILLAGES("Négocier avec les villages"),
    CALM_CONFLICT("Apaiser un conflit"),
    ORGANIZE_PARTY("Organiser une fête"),
	

	// Brainy Smurf
    READ_A_BOOK("Lire un livre"),
    TEACH_A_CLASS("Donner un cours"),
    GIVE_A_SPEECH("Faire un discours"),
	
	// Jokey Smurf
	GIVE_GIFT("Offrir un cadeau piégé"),
	SET_UP_PRANK("Préparer une farce"),
	MAKE_FIREWORK("Fabriquer un feu d'artifice"),
	
	// Miner Smurf
	MINE_GOLD("Miner de l'or"),
	REINFORCE_TOOLS("Renforcer les outils"),
	SECURE_TUNNEL("Sécuriser un tunnel"),
	
	// Magician Smurf
	TRANSMUTE_TO_GOLD("Transmuter des baies en or"),
	TRANSMUTE_TO_KNOWLEDGE("Transmuter de la salsepareille en savoir"),
	TRANSMUTE_TO_DEFENSE("Transmuter des outils en défense");
	

    private final String displayName;

    ActionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
    
    public Map<Resource, Integer> getExpectedEffects() {
        return switch (this) {
            // Papa Smurf
            case CONSULT_GRIMOIRE        -> Map.of(Resource.KNOWLEDGE, 2, Resource.MORALE, -1);
            case ORGANIZE_MEETING        -> Map.of(Resource.MORALE, 2);
            case NEGOTIATE_WITH_ANIMALS  -> Map.of(Resource.GOLD, 1, Resource.DEFENSE, 1);
            // Handy Smurf
            case REPAIR_HOUSES           -> Map.of(Resource.TOOLS, 2, Resource.SARSAPARILLA, -1);
            case BUILD_TRAP              -> Map.of(Resource.DEFENSE, 2, Resource.TOOLS, -1);
            case INVENT_GADGET           -> Map.of();
            // Greedy Smurf
            case PICK_BERRIES            -> Map.of(Resource.BERRIES, 3);
            case ORGANIZE_FEAST          -> Map.of(Resource.BERRIES, -2, Resource.MORALE, 2);
            case FIND_RARE_MUSHROOM      -> Map.of();
            // Smurfette
            case NEGOTIATE_WITH_VILLAGES -> Map.of();
            case CALM_CONFLICT           -> Map.of(Resource.MORALE, 2);
            case ORGANIZE_PARTY          -> Map.of(Resource.BERRIES, -2, Resource.MORALE, 3);
            // Brainy Smurf
            case READ_A_BOOK             -> Map.of(Resource.KNOWLEDGE, 2, Resource.MORALE, -1);
            case TEACH_A_CLASS           -> Map.of(Resource.KNOWLEDGE, 2);
            case GIVE_A_SPEECH           -> Map.of(Resource.MORALE, 1);
            // Jokey Smurf
            case GIVE_GIFT               -> Map.of();
            case SET_UP_PRANK            -> Map.of(Resource.MORALE, -2, Resource.BERRIES, 1);
            case MAKE_FIREWORK           -> Map.of(Resource.TOOLS, -1, Resource.MORALE, 2);
            // Miner Smurf
            case MINE_GOLD               -> Map.of(Resource.TOOLS, -1, Resource.GOLD, 2);
            case REINFORCE_TOOLS         -> Map.of(Resource.GOLD, -1, Resource.TOOLS, 2);
            case SECURE_TUNNEL           -> Map.of(Resource.TOOLS, -1, Resource.DEFENSE, 2);
            // Magician Smurf
            case TRANSMUTE_TO_GOLD       -> Map.of(Resource.BERRIES, -2, Resource.GOLD, 2);
            case TRANSMUTE_TO_KNOWLEDGE  -> Map.of(Resource.SARSAPARILLA, -2, Resource.KNOWLEDGE, 2);
            case TRANSMUTE_TO_DEFENSE    -> Map.of(Resource.TOOLS, -2, Resource.DEFENSE, 2);
        };
    }
}