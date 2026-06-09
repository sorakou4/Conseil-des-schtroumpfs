package vue;

import java.util.List;
import java.util.Scanner;

import modele.ActionType;
import modele.Character;
import modele.Event;
import modele.Resource;
import modele.SmurfVillage;

public class ConsoleView {

    private Scanner scanner;

    public ConsoleView() {
        scanner = new Scanner(System.in);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayResources(SmurfVillage village) {
        for (Resource resource : Resource.values()) {
            System.out.println(resource + " : " + village.getQuantity(resource));
        }
    }

    public void displayEvent(Event event) {
        System.out.println("\nRandom event: " + event.getDescription());
    }

    public Character chooseCharacter(List<Character> characters) {
        System.out.println("\nChoose a character:");

        for (int i = 0; i < characters.size(); i++) {
            System.out.println((i + 1) + " - " + characters.get(i).getName());
        }

        int choice = readChoice(1, characters.size());

        return characters.get(choice - 1);
    }

    public ActionType chooseAction(Character character) {
        List<ActionType> actions = character.getAvailableActions();

        System.out.println("\nChoose an action:");

        for (int i = 0; i < actions.size(); i++) {
            System.out.println((i + 1) + " - " + actions.get(i));
        }

        int choice = readChoice(1, actions.size());

        return actions.get(choice - 1);
    }
    
    private int readChoice(int min, int max) {
        int choice = scanner.nextInt();

        while (choice < min || choice > max) {
            System.out.println("Invalid choice. Try again:");
            choice = scanner.nextInt();
        }

        return choice;
    }
}