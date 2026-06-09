package modele;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class HandySmurf implements Character {

    private String name;

    private Random random;

    public HandySmurf() {

        name = "Schtroumpf Bricoleur";

        random = new Random();
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {

        return List.of(
                ActionType.REPAIR_HOUSES,
                ActionType.BUILD_TRAP,
                ActionType.INVENT_GADGET
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
                    "Action indisponible pour le Schtroumpf Bricoleur"
            );
        }

        switch(action) {

            case REPAIR_HOUSES:

                if (!village.hasEnoughResource(
                        Resource.SARSAPARILLA,
                        1
                )) {

                    throw new IllegalStateException(
                            "Pas assez de Salsepareille"
                    );
                }

                village.modifyResource(
                        Resource.TOOLS,
                        2
                );

                village.modifyResource(
                        Resource.SARSAPARILLA,
                        -1
                );

                break;

            case BUILD_TRAP:

                if (!village.hasEnoughResource(
                        Resource.TOOLS,
                        1
                )) {

                    throw new IllegalStateException(
                            "Pas assez d'outils pour fabriquer un piège"
                    );
                }

                village.modifyResource(
                        Resource.DEFENSE,
                        2
                );

                village.modifyResource(
                        Resource.TOOLS,
                        -1
                );

                break;

            case INVENT_GADGET:

                int bonus = random.nextInt(3);

                if (bonus == 0) {

                    village.modifyResource(
                            Resource.TOOLS,
                            2
                    );

                } else if (bonus == 1) {

                    village.modifyResource(
                            Resource.DEFENSE,
                            2
                    );

                } else {

                    village.modifyResource(
                            Resource.MORALE,
                            -1
                    );
                }

                break;

            default:

                throw new IllegalArgumentException(
                        "Action inconnue"
                );
        }
    }
}