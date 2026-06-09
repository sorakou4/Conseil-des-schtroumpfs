package modele;

import java.util.List;
import java.util.Objects;

public record MagicianSmurf(String name) implements Character {

    public MagicianSmurf() {
        this("Schtroumpf Magicien");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {
        return List.of(
                ActionType.TRANSMUTE_TO_GOLD,
                ActionType.TRANSMUTE_TO_KNOWLEDGE,
                ActionType.TRANSMUTE_TO_DEFENSE
        );
    }

    @Override
    public void playAction(SmurfVillage village, ActionType action) {

        Objects.requireNonNull(village);
        Objects.requireNonNull(action);

        if (!getAvailableActions().contains(action)) {
            throw new IllegalArgumentException(
                    "Action indisponible pour le Schtroumpf Magicien"
            );
        }

        switch(action) {

            case TRANSMUTE_TO_GOLD:

                if (!village.hasEnoughResource(Resource.BERRIES, 2)) {
                    throw new IllegalStateException(
                            "Pas assez de baies pour effectuer la transmutation"
                    );
                }

                village.modifyResource(Resource.BERRIES, -2);
                village.modifyResource(Resource.GOLD, 2);
                break;

            case TRANSMUTE_TO_KNOWLEDGE:

                if (!village.hasEnoughResource(Resource.SARSAPARILLA, 2)) {
                    throw new IllegalStateException(
                            "Pas assez de salsepareille pour effectuer la transmutation"
                    );
                }

                village.modifyResource(Resource.SARSAPARILLA, -2);
                village.modifyResource(Resource.KNOWLEDGE, 2);
                break;

            case TRANSMUTE_TO_DEFENSE:

                if (!village.hasEnoughResource(Resource.TOOLS, 2)) {
                    throw new IllegalStateException(
                            "Pas assez d'outils pour effectuer la transmutation"
                    );
                }

                village.modifyResource(Resource.TOOLS, -2);
                village.modifyResource(Resource.DEFENSE, 2);
                break;

            default:
                throw new IllegalArgumentException("Action inconnue");
        }
    }
}
