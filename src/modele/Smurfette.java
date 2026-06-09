package modele;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Smurfette implements Character {

    private String name;

    private Random random;

    public Smurfette() {

        name = "Schtroumpfette";

        random = new Random();
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {

        return List.of(
                ActionType.NEGOTIATE_WITH_VILLAGES,
                ActionType.CALM_CONFLICT,
                ActionType.ORGANIZE_PARTY
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
                    "Action indisponible pour la Schtroumpfette"
            );
        }

        switch(action) {

            case NEGOTIATE_WITH_VILLAGES:

                if (random.nextBoolean()) {

                    village.modifyResource(
                            Resource.GOLD,
                            2
                    );

                } else {

                    village.modifyResource(
                            Resource.SARSAPARILLA,
                            2
                    );
                }

                break;

            case CALM_CONFLICT:

                village.modifyResource(
                        Resource.MORALE,
                        2
                );

                break;

            case ORGANIZE_PARTY:

                if (!village.hasEnoughResource(
                        Resource.BERRIES,
                        2
                )) {

                    throw new IllegalStateException(
                            "Pas assez de baies pour organiser une fête"
                    );
                }

                village.modifyResource(
                        Resource.BERRIES,
                        -2
                );

                village.modifyResource(
                        Resource.MORALE,
                        3
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Action inconnue"
                );
        }
    }
}