package controleur;

import java.util.*;
import modele.*;
import vue.GameViewController;

public class JavaFXController {

    private SmurfVillage       village;
    private GameViewController view;
    private EventGenerator     eventGenerator;

    private int    currentMonth;
    private Event  currentEvent;
    private String popupMode;
    private String villageName;

    private String firstCharName    = "";
    private String firstActionName  = "";
    private String secondCharName   = "";
    private String secondActionName = "";

    public JavaFXController(SmurfVillage village, GameViewController view, String villageName) {
        this.village        = village;
        this.view           = view;
        this.villageName    = villageName;
        this.eventGenerator = new EventGenerator();
        this.currentMonth   = 1;
        this.popupMode      = "NONE";

        view.setCurrentVillage(village);
        view.setCurrentMonth(currentMonth);
        view.displayCharacters(village.getCharacters());

        updateActions();
        startMonth();

        view.getCharacterChoice1().setOnAction(e -> updateActions());
        view.getCharacterChoice2().setOnAction(e -> updateActions());
        view.getActionChoice1().getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> updateEffectsPreview());
        view.getActionChoice2().getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> updateEffectsPreview());
        view.getCharacterChoice1().getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateEffectsPreview());
        view.getCharacterChoice2().getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateEffectsPreview());
        view.getValidateButton().setOnAction(e -> playActions());
        view.getContinueButton().setOnAction(e -> handleContinueButton());
    }
    
    private void updateEffectsPreview() {
        ActionType a1 = null, a2 = null;

        String name1 = view.getCharacterChoice1().getValue();
        String name2 = view.getCharacterChoice2().getValue();
        int ai1 = view.getFirstActionIndex();
        int ai2 = view.getSecondActionIndex();

        for (modele.Character c : village.getCharacters()) {
            if (c.getName().equals(name1) && ai1 >= 0 && ai1 < c.getAvailableActions().size())
                a1 = c.getAvailableActions().get(ai1);
            if (c.getName().equals(name2) && ai2 >= 0 && ai2 < c.getAvailableActions().size())
                a2 = c.getAvailableActions().get(ai2);
        }

        view.showActionEffects(a1, a2);
    }

    public void setCurrentMonth(int month) {
        this.currentMonth = month;
        view.setCurrentMonth(month);
        startMonth();
    }


    private void startMonth() {
        if (currentMonth > 12) {
            endGame();
            return;
        }

        view.displayMonth(currentMonth);

        String productionText = village.produceResources();
        village.consumeResources();
        view.displayResources(village);
        view.displayMessage("Début du mois " + currentMonth + ". Choisissez vos actions.");

        currentEvent = eventGenerator.generateRandomEvent();
        village.addEventToHistory(currentEvent);
        currentEvent.applyEvent(village);
        village.updateCrises();
        view.displayResources(village);

        if (isDefeat()) {
            showDefeat();
            return;
        }
        popupMode = "EVENT";

        // Crises actives à afficher dans le popup
        List<CrisisType> crisesNow = village.getCurrentCrises();
        String crisesText;
        if (crisesNow.isEmpty()) {
            crisesText = "✅ Aucune crise active.";
        } else {
            StringBuilder sbC = new StringBuilder("Crises actives :\n");
            for (CrisisType ct : crisesNow) sbC.append("• ").append(ct).append("\n");
            crisesText = sbC.toString();
        }

        String popupText = currentEvent.getDescription() + "\n\n"
        	    + currentEvent.getEffectDescription() + "\n\n"
        	    + "── Production ──\n"
        	    + productionText + "\n\n"
        	    + "── État du village ──\n"
        	    + crisesText;

        // Passe la description de l'événement séparément pour le label central
        view.showEventPopup(popupText);
        }

    private void handleContinueButton() {
    	if (popupMode.equals("PRODUCTION")) {

        	popupMode = "EVENT";

            // Génère un événement aléatoire
            currentEvent = eventGenerator.generateRandomEvent();

            // Ajoute l'événement à l'historique du village
            village.addEventToHistory(currentEvent);
            
            // Applique les effets de l'événement sur les ressources
            currentEvent.applyEvent(village);

            // Met à jour les crises éventuelles
            village.updateCrises();

            // Rafraîchit l'affichage des ressources et messages
            view.displayResources(village);
            view.displayEvent(currentEvent.getDescription());
            view.displayMessage(village.getCrisesDescription());

            // Vérifie si le joueur a perdu
            if (isDefeat()) {
                showDefeat();
                return;
            }
            // Construit le texte complet de la popup d'événement
            String eventText =
                    currentEvent.getDescription()
                            + "\n\n"
                            + currentEvent.getEffectDescription()
                            + "\n\n"
                            + village.getCrisesDescription();

            // Affiche la popup d'événement
            view.showEventPopup(eventText);

        // Si on sort de la popup ÉVÉNEMENT
        } else if (popupMode.equals("EVENT")) {

            popupMode = "NONE";

            view.hideEventPopup();

            view.displayMessage(
                    "Choisissez deux Schtroumpfs différents.\n\n"
                            + village.getCrisesDescription()
            );


        } else if ("CRISIS".equals(popupMode)) {
            village.applyCrisisEffects();
            view.displayResources(village);
            popupMode = "NONE";
            currentMonth++;
            startMonth();
        }
    }

    private void updateActions() {
        List<modele.Character> chars = village.getCharacters();

        String name1 = view.getCharacterChoice1().getValue();
        String name2 = view.getCharacterChoice2().getValue();

        view.getActionChoice1().getItems().clear();
        view.getActionChoice2().getItems().clear();

        for (modele.Character c : chars) {
            if (c.getName().equals(name1)) {
                view.displayActionsForFirstCharacter(c.getAvailableActions());
            }
            if (c.getName().equals(name2)) {
                view.displayActionsForSecondCharacter(c.getAvailableActions());
            }
        }
    }

    private void playActions() {
        String name1 = view.getCharacterChoice1().getValue();
        String name2 = view.getCharacterChoice2().getValue();

        int ai1 = view.getFirstActionIndex();
        int ai2 = view.getSecondActionIndex();

        if (name1 == null || name2 == null || ai1 < 0 || ai2 < 0) {
            view.displayMessage("Erreur : Vous devez sélectionner 2 Schtroumpfs et leurs actions !");
            return;
        }

        if (name1.equals(name2)) {
            view.displayMessage("Erreur : Un même Schtroumpf ne peut pas faire deux actions en même temps !");
            return;
        }

        List<modele.Character> chars = village.getCharacters();
        modele.Character c1 = null, c2 = null;

        for (modele.Character c : chars) {
            if (c.getName().equals(name1)) c1 = c;
            if (c.getName().equals(name2)) c2 = c;
        }

        if (c1 == null || c2 == null) return;

        List<ActionType> actions1 = c1.getAvailableActions();
        List<ActionType> actions2 = c2.getAvailableActions();

        if (ai1 >= actions1.size() || ai2 >= actions2.size()) {
            view.displayMessage("Erreur : index d'action invalide.");
            return;
        }

        ActionType a1 = actions1.get(ai1);
        ActionType a2 = actions2.get(ai2);

        try {
            c1.playAction(village, a1);
            c2.playAction(village, a2);
        } catch (IllegalStateException e) {
            view.displayMessage("Action impossible : " + e.getMessage());
            return;
        }

        firstCharName    = name1;
        firstActionName  = a1.toString();
        secondCharName   = name2;
        secondActionName = a2.toString();

        village.updateCrises();
        view.displayResources(village);

        // CORRECTION : mise à jour du message avec les crises actives
        List<CrisisType> crises = village.getCurrentCrises();
        if (!crises.isEmpty()) {
            StringBuilder sb = new StringBuilder("⚠️ Crises actives : ");
            for (CrisisType ct : crises) sb.append(ct).append("  ");
            view.displayMessage(sb.toString());
        } else {
            view.displayMessage("Mois " + currentMonth + " terminé. Aucune crise.");
        }

        List<String> criseNames = new ArrayList<>();
        for (CrisisType ct : crises) {
            criseNames.add(ct.toString());
        }
        Map<String, Integer> resSnapshot = new LinkedHashMap<>();
        for (modele.Resource r : modele.Resource.values()) {
            resSnapshot.put(r.name(), village.getQuantity(r));
        }
        TurnRecord record = new TurnRecord(
            currentMonth,
            currentEvent != null ? currentEvent.getDescription() : "—",
            firstCharName, firstActionName,
            secondCharName, secondActionName,
            criseNames,
            resSnapshot,
            village.calculateScore()
        );
        view.addTurnRecord(record);

        if (crises.size() >= 3) {
            SaveManager.addScore(villageName, village.calculateScore(), currentMonth);
            showDefeat();
            return;
        }

        if (!crises.isEmpty()) {
            popupMode = "CRISIS";
            view.showCrisisPopup(village.getCrisisPenaltyDescription());
        } else {
            currentMonth++;
            startMonth();
        }
    }

    private boolean isDefeat() { return village.getCurrentCrises().size() >= 3; }

    private void showDefeat() {
        view.hideEventPopup();
        view.displayResources(village);
        SaveManager.addScore(villageName, village.calculateScore(), currentMonth);
        view.getValidateButton().setDisable(true);
        view.showEndScreen(false, village.calculateScore());
    }

    private void endGame() {
        view.hideEventPopup();
        view.displayMonth(12);
        view.displayResources(village);
        village.updateCrises();
        int score = village.calculateScore();
        SaveManager.addScore(villageName, score, 12);
        view.getValidateButton().setDisable(true);
        view.showEndScreen(!isDefeat(), score);
    }
    
}
