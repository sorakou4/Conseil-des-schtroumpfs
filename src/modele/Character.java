package modele;

import java.util.List;

public interface Character {

    String getName();

    List<ActionType> getAvailableActions();

    void playAction(SmurfVillage village, ActionType action);
}