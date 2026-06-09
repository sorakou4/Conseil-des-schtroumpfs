package modele;

import java.util.List;
import java.util.Objects;

public record PapaSmurf(String name) implements Character {

    public PapaSmurf() {
        this("Grand Schtroumpf");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {
        return List.of(
                ActionType.CONSULT_GRIMOIRE,
                ActionType.ORGANIZE_MEETING,
                ActionType.NEGOTIATE_WITH_ANIMALS
        );
    }

    @Override
    public void playAction(SmurfVillage village, ActionType action) {

        Objects.requireNonNull(village);
        Objects.requireNonNull(action);

        if (!getAvailableActions().contains(action)) {
            throw new IllegalArgumentException(
                    "Action indisponible pour le Grand Schtroumpf"
            );
        }

        switch(action) {

            case CONSULT_GRIMOIRE:

                if (!village.hasEnoughResource(Resource.MORALE, 1)) {
                    throw new IllegalStateException(
                            "Pas assez de moral pour consulter le grimoire"
                    );
                }

                village.modifyResource(Resource.KNOWLEDGE, 2);
                village.modifyResource(Resource.MORALE, -1);
                break;

            case ORGANIZE_MEETING:

                village.modifyResource(Resource.MORALE, 2);
                break;

            case NEGOTIATE_WITH_ANIMALS:

                village.modifyResource(Resource.GOLD, 1);
                village.modifyResource(Resource.DEFENSE, 1);
                break;

            default:
                throw new IllegalArgumentException("Action inconnue");
        }
    }
}
