package modele;

import java.util.List;
import java.util.Objects;

public record MinerSmurf(String name) implements Character {

    public MinerSmurf() {
        this("Schtroumpf Mineur");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {
        return List.of(
                ActionType.MINE_GOLD,
                ActionType.REINFORCE_TOOLS,
                ActionType.SECURE_TUNNEL
        );
    }

    @Override
    public void playAction(SmurfVillage village, ActionType action) {

        Objects.requireNonNull(village);
        Objects.requireNonNull(action);

        if (!getAvailableActions().contains(action)) {
            throw new IllegalArgumentException(
                    "Action indisponible pour le Schtroumpf Mineur"
            );
        }

        switch(action) {

            case MINE_GOLD:

                if (!village.hasEnoughResource(Resource.TOOLS, 1)) {
                    throw new IllegalStateException(
                            "Pas assez d'outils pour miner de l'or"
                    );
                }

                village.modifyResource(Resource.TOOLS, -1);
                village.modifyResource(Resource.GOLD, 2);
                break;

            case REINFORCE_TOOLS:

                if (!village.hasEnoughResource(Resource.GOLD, 1)) {
                    throw new IllegalStateException(
                            "Pas assez d'or pour renforcer les outils"
                    );
                }

                village.modifyResource(Resource.GOLD, -1);
                village.modifyResource(Resource.TOOLS, 2);
                break;

            case SECURE_TUNNEL:

                if (!village.hasEnoughResource(Resource.TOOLS, 1)) {
                    throw new IllegalStateException(
                            "Pas assez d'outils pour sécuriser un tunnel"
                    );
                }

                village.modifyResource(Resource.TOOLS, -1);
                village.modifyResource(Resource.DEFENSE, 2);
                break;

            default:
                throw new IllegalArgumentException("Action inconnue");
        }
    }
}
