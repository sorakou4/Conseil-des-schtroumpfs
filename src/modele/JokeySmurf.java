package modele;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class JokeySmurf implements Character {

    private String name;

    private Random random;

    public JokeySmurf() {

        name = "Schtroumpf Farceur";

        random = new Random();
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {

        return List.of(
                ActionType.GIVE_GIFT,
                ActionType.SET_UP_PRANK,
                ActionType.MAKE_FIREWORK
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
                    "Action indisponible pour le Schtroumpf Farceur"
            );
        }

        switch(action) {

            case GIVE_GIFT:

                if (random.nextBoolean()) {

                    village.modifyResource(
                            Resource.MORALE,
                            2
                    );

                } else {

                    village.modifyResource(
                            Resource.MORALE,
                            -1
                    );
                }

                break;

            case SET_UP_PRANK:

                if (!village.hasEnoughResource(
                        Resource.MORALE,
                        1
                )) {

                    throw new IllegalStateException(
                            "Pas assez de moral pour préparer une farce"
                    );
                }

                village.modifyResource(
                        Resource.MORALE,
                        -2
                );

                village.modifyResource(
                        Resource.BERRIES,
                        1
                );

                break;

            case MAKE_FIREWORK:

                if (!village.hasEnoughResource(
                        Resource.TOOLS,
                        1
                )) {

                    throw new IllegalStateException(
                            "Pas assez d'outils pour fabriquer un feu d'artifice"
                    );
                }

                village.modifyResource(
                        Resource.TOOLS,
                        -1
                );

                village.modifyResource(
                        Resource.MORALE,
                        2
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Action inconnue"
                );
        }
    }
}
