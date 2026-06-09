package modele;

import java.util.List;
import java.util.Objects;

public class BrainySmurf implements Character {

    private String name;

    public BrainySmurf() {

        name = "Schtroumpf à Lunettes";

    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {

        return List.of(
                ActionType.READ_A_BOOK,
                ActionType.TEACH_A_CLASS,
                ActionType.GIVE_A_SPEECH
        );
    }

    @Override
    public void playAction(
            SmurfVillage village,
            ActionType action
    ) {

        Objects.requireNonNull(village);

        Objects.requireNonNull(action);

        if (!getAvailableActions().contains(action)) {

            throw new IllegalArgumentException(
                    "Action indisponible pour le Schtroumpf à Lunettes"
            );
        }

        switch(action) {

            case READ_A_BOOK:

                if (!village.hasEnoughResource(
                        Resource.MORALE,
                        1
                )) {

                    throw new IllegalStateException(
                            "Pas assez de moral pour lire un livre"
                    );
                }

                village.modifyResource(
                        Resource.KNOWLEDGE,
                        2
                );

                village.modifyResource(
                        Resource.MORALE,
                        -1
                );

                break;

            case TEACH_A_CLASS:

                if (!village.hasEnoughResource(
                        Resource.KNOWLEDGE,
                        1
                )) {

                    throw new IllegalStateException(
                            "Pas assez de savoir pour donner un cours"
                    );
                }

                village.modifyResource(
                        Resource.KNOWLEDGE,
                        2
                );

                break;

            case GIVE_A_SPEECH:

                village.modifyResource(
                        Resource.MORALE,
                        1
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Action inconnue"
                );
        }
    }
}
