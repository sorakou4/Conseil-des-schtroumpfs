package controleur;

import java.util.List;

import modele.ActionType;
import modele.Character;
import modele.CrisisType;
import modele.Event;
import modele.EventGenerator;
import modele.SmurfVillage;
import vue.ConsoleView;

public class GameController {

    private SmurfVillage village;
    private EventGenerator eventGenerator;
    private ConsoleView view;

    public GameController() {
        village = new SmurfVillage();
        eventGenerator = new EventGenerator();
        view = new ConsoleView();
    }

    public void startGame() {

        for (int turn = 1; turn <= 12; turn++) {

            view.displayMessage("\n===== MONTH " + turn + " =====");

            village.updateCrises();

            view.displayMessage("\nResources at the beginning:");
            view.displayResources(village);

            view.displayMessage("\n" + village.getCrisesDescription());

            view.displayMessage("\nProduction phase...");
            String productionText = village.produceResources();
            view.displayMessage(productionText);

            village.updateCrises();

            view.displayMessage("\nResources after production:");
            view.displayResources(village);

            Event event = eventGenerator.generateRandomEvent();
            village.addEventToHistory(event);

            view.displayEvent(event);

            event.applyEvent(village);

            village.updateCrises();

            view.displayMessage("\nResources after event:");
            view.displayResources(village);

            view.displayMessage("\n" + village.getCrisesDescription());

            view.displayMessage("\nFirst council action:");
            Character firstCharacter = playCouncilAction(null);

            view.displayMessage("\nSecond council action:");
            playCouncilAction(firstCharacter);

            village.updateCrises();

            view.displayMessage("\nResources after council actions:");
            view.displayResources(village);

            view.displayMessage("\nConsumption phase...");
            village.consumeResources();

            village.updateCrises();

            view.displayMessage("\nResources after consumption:");
            view.displayResources(village);

            if (!village.getCurrentCrises().isEmpty()) {

                view.displayMessage("\nCrisis effects:");
                village.applyCrisisEffects();

                village.updateCrises();

                view.displayMessage("\nResources after crisis effects:");
                view.displayResources(village);

                view.displayMessage("\n" + village.getCrisesDescription());
            }

            List<CrisisType> crises = village.getCurrentCrises();

            if (crises.size() >= 3) {
                view.displayMessage("\nDefeat! Too many crises in the village.");
                view.displayMessage("Final score: " + village.calculateScore());
                return;
            }
        }

        endGame();
    }

    private Character playCouncilAction(Character forbiddenCharacter) {

        Character character = view.chooseCharacter(village.getCharacters());

        while (character == forbiddenCharacter) {
            view.displayMessage("This character has already played this month.");
            character = view.chooseCharacter(village.getCharacters());
        }

        view.displayMessage("\nCouncil action:");
        view.displayMessage(character.getName() + " plays.");

        ActionType action = view.chooseAction(character);

        view.displayMessage("Chosen action: " + action);

        try {
            character.playAction(village, action);
        } catch (IllegalStateException exception) {
            view.displayMessage("Impossible action: " + exception.getMessage());
            return playCouncilAction(forbiddenCharacter);
        }

        return character;
    }

    private void endGame() {

        view.displayMessage("\nEnd of the game!");

        view.displayMessage("\nEvent history:");

        for (Event event : village.getEventHistory()) {
            view.displayMessage("- " + event.getDescription());
        }

        int finalScore = village.calculateScore();

        view.displayMessage("\nFinal score: " + finalScore);

        village.updateCrises();

        if (village.getCurrentCrises().size() >= 3) {
            view.displayMessage("Defeat! The village collapsed.");
        } else {
            view.displayMessage("Victory! The Smurf village survived 12 months.");
        }
    }
}