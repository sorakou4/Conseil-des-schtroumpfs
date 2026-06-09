package modele;

import java.util.List;
import java.util.Objects;
import java.util.Random;

// Classe représentant le Schtroumpf Gourmand
// Spécialisé dans la nourriture et le moral du village
public class GreedySmurf implements Character {

    private String name;

    private Random random;

    public GreedySmurf() {

        name = "Schtroumpf Gourmand";

        random = new Random();
    }

    @Override
    public String getName() {

        // Retourne le nom du personnage
        return name;
    }

    @Override
    public List<ActionType> getAvailableActions() {

        // Liste des actions possibles
        return List.of(
                ActionType.PICK_BERRIES,
                ActionType.ORGANIZE_FEAST,
                ActionType.FIND_RARE_MUSHROOM
        );
    }

    @Override
    public void playAction(SmurfVillage village, ActionType action) {

        Objects.requireNonNull(village);
        Objects.requireNonNull(action);

        // Empêche d'exécuter une action non autorisée
        if (!getAvailableActions().contains(action)) {
            throw new IllegalArgumentException(
                    "Action indisponible pour le Schtroumpf Gourmand"
            );
        }

        // Exécution de l'action
        switch(action) {

            case PICK_BERRIES:

                // Récolte de baies
                village.modifyResource(Resource.BERRIES, 3);
                break;

            case ORGANIZE_FEAST:

                // Vérifie qu'il y a assez de baies pour organiser un festin
                if (!village.hasEnoughResource(Resource.BERRIES, 2)) {
                    throw new IllegalStateException(
                            "Pas assez de baies pour organiser un festin"
                    );
                }

                // Le festin consomme des baies
                village.modifyResource(Resource.BERRIES, -2);

                // mais augmente le moral
                village.modifyResource(Resource.MORALE, 2);
                break;

            case FIND_RARE_MUSHROOM:

                // Bonus aléatoire (0, 1 ou 2)
                int bonus = random.nextInt(3);

                if (bonus == 0) {
                    village.modifyResource(Resource.BERRIES, 2);

                } else if (bonus == 1) {
                    village.modifyResource(Resource.SARSAPARILLA, 2);

                } else {
                    village.modifyResource(Resource.KNOWLEDGE, 1);
                }

                break;

            default:

                throw new IllegalArgumentException("Action inconnue");
        }
    }
}
