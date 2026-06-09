package vue;

import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import modele.*;
import vue.ShopViewController;

public class GameViewController {

    // Éléments principaux
    @FXML private StackPane root;
    @FXML private BorderPane gamePane;
    @FXML private Label titleLabel;
    @FXML private Label monthLabel;
    @FXML private Label eventLabel;
    @FXML private Label messageLabel;
    @FXML private VBox resourcesBox;

    @FXML private ChoiceBox<String> characterChoice1;
    @FXML private ChoiceBox<String> actionChoice1;
    @FXML private ChoiceBox<String> characterChoice2;
    @FXML private ChoiceBox<String> actionChoice2;

    @FXML private ImageView characterImage1;
    @FXML private ImageView characterImage2;

    @FXML private Button validateButton;
    @FXML private Button recapButton;

    // Popup événement 
    @FXML private StackPane eventPopup;
    @FXML private Label eventPopupText;
    @FXML private Button continueButton;

    // Menu in-game
    @FXML private StackPane ingameMenuOverlay;

    // Sauvegarde
    @FXML private StackPane saveOverlay;
    @FXML private Button saveSlot1;
    @FXML private Button saveSlot2;
    @FXML private Button saveSlot3;
    @FXML private Button saveSlot4;

    // Récapitulatif
    @FXML private StackPane recapOverlay;
    @FXML private VBox recapContent;

    // Paramètres in-game
    @FXML private VBox ingameSettingsPanel;
    @FXML private Button cbNone;
    @FXML private Button cbDeuter;
    @FXML private Button cbProtan;
    @FXML private Button cbTritan;
    @FXML private Button cbAchro;
    @FXML private Slider volumeSlider;
    @FXML private Slider sfxSlider;

    // Règles in-game
    @FXML private StackPane ingameRulesPanel;
    @FXML private Label ingameRulesTitle;
    @FXML private Label ingameRulesText;
    @FXML private ImageView ingameRulesCharImage;
    @FXML private Button ingameRulesPrevBtn;
    @FXML private Button ingameRulesNextBtn;
    
    // Fin de jeu
    @FXML private StackPane endScreenOverlay;
    @FXML private Label endScreenTitle;
    @FXML private Label endScreenScore;
    @FXML private Button endRecapButton;

    // État interne
    private final Map<Resource, Label> resourceLabels = new HashMap<>();
    private final Map<Resource, HBox> resourceRows = new HashMap<>();
    private final List<TurnRecord>  turnHistory    = new ArrayList<>();

    private SmurfVillage currentVillage;
    private String currentVillageName = "";
    private int currentMonth = 1;

    private Runnable onQuitToHome;

    // Règles
    private List<RulePage> rulePages;
    private int currentRulePage;


    @FXML
    public void initialize() {
    	
        resourcesBox.getChildren().clear();
    	for (Resource r : Resource.values()) {
            HBox row = new HBox(8);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            javafx.scene.image.ImageView img = new javafx.scene.image.ImageView();
            img.setFitWidth(28);
            img.setFitHeight(28);
            img.setPreserveRatio(true);
            try {
                img.setImage(new javafx.scene.image.Image(
                    getClass().getResource("/images/" + r.name().toLowerCase() + ".png").toExternalForm()));
            } catch (Exception ignored) {}

            Label lbl = new Label();
            lbl.getStyleClass().add("resource-label-normal");

            Label indicator = new Label();
            indicator.getStyleClass().add("resource-indicator");
            indicator.setVisible(false);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().addAll(lbl, indicator, spacer, img);
            resourcesBox.getChildren().add(row);
            resourceLabels.put(r, lbl);
            resourceRows.put(r, row);
        }

        characterChoice1.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> updateCharacterImage(characterImage1, n));
        characterChoice2.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> updateCharacterImage(characterImage2, n));

        eventPopup.setVisible(false);
        ingameMenuOverlay.setVisible(false);
        saveOverlay.setVisible(false);
        recapOverlay.setVisible(false);
        ingameSettingsPanel.setVisible(false);
        ingameRulesPanel.setVisible(false);
        endScreenOverlay.setVisible(false);

        createRulePages();
        
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e ->
                    MediaManager.playSfx("/music/click.wav"));
                newScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e ->
                    MediaManager.playSfx("/music/click.wav"));
            }
        });

        if (volumeSlider != null) {
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                MediaManager.setVolume(newVal.doubleValue() / 100.0));
        }

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) toggleIngameMenu();
                });
            }
        });
    }
    
    public void showActionEffects(ActionType action1, ActionType action2) {
        for (Resource r : Resource.values()) {
            HBox row = resourceRows.get(r);
            if (row != null && row.getChildren().size() >= 3) {
                Label indicator = (Label) row.getChildren().get(1);
                indicator.setVisible(false);
            }
        }

        Map<Resource, Integer> combined = new LinkedHashMap<>();
        if (action1 != null) action1.getExpectedEffects().forEach(
            (r, v) -> combined.merge(r, v, Integer::sum));
        if (action2 != null) action2.getExpectedEffects().forEach(
            (r, v) -> combined.merge(r, v, Integer::sum));

        for (Map.Entry<Resource, Integer> entry : combined.entrySet()) {
            HBox row = resourceRows.get(entry.getKey());
            if (row == null || row.getChildren().size() < 3) continue;
            Label indicator = (Label) row.getChildren().get(1);
            int val = entry.getValue();
            indicator.setText(val > 0 ? "+" + val : String.valueOf(val));
            indicator.getStyleClass().removeAll("resource-indicator-plus", "resource-indicator-minus");
            indicator.getStyleClass().add(val > 0 ? "resource-indicator-plus" : "resource-indicator-minus");
            indicator.setVisible(true);
        }
    }

    // Setters

    public void setVillageName(String name) {
        currentVillageName = name;
        titleLabel.setText("Village " + name);
    }

    public void setCurrentVillage(SmurfVillage v) { currentVillage = v; }
    public void setCurrentMonth(int m)            { currentMonth = m; }
    public void setOnQuitToHome(Runnable r)       { onQuitToHome = r; }
    
    private Runnable onShopPurchased;
    public void setOnShopPurchased(Runnable r) { onShopPurchased = r; }

    public void syncColorBlindButtons(ColorBlindManager.ColorBlindType active) {
        updateColorBlindButtons(active);
    }

    // Display
    public void displayMonth(int month) {
        currentMonth = month;
        monthLabel.setText("Mois " + month + " / 12");
    }

    public void displayResources(SmurfVillage village) {
        currentVillage = village;
        for (Resource r : Resource.values()) {
            int qty   = village.getQuantity(r);
            Label lbl = resourceLabels.get(r);
            lbl.setText(r + " : " + qty);
            lbl.getStyleClass().removeAll(
                    "resource-label-normal",
                    "resource-label-warning",
                    "resource-label-crisis");
            if      (qty == 0) lbl.getStyleClass().add("resource-label-crisis");
            else if (qty <= 2) lbl.getStyleClass().add("resource-label-warning");
            else               lbl.getStyleClass().add("resource-label-normal");
        }
    }

    public void displayEvent(String text)   { eventLabel.setText(text); }
    public void displayMessage(String text) { messageLabel.setText(text); }

    public void displayCharacters(List<modele.Character> characters) {
        characterChoice1.getItems().clear();
        characterChoice2.getItems().clear();
        for (modele.Character c : characters) {
            characterChoice1.getItems().add(c.getName());
            characterChoice2.getItems().add(c.getName());
        }
        characterChoice1.getSelectionModel().selectFirst();
        if (characters.size() > 1)
            characterChoice2.getSelectionModel().select(1);
        else
            characterChoice2.getSelectionModel().selectFirst();
    }

    public void displayActionsForFirstCharacter(List<ActionType> actions) {
        actionChoice1.getItems().setAll(actions.stream().map(ActionType::toString).toList());
        actionChoice1.getSelectionModel().selectFirst();
    }

    public void displayActionsForSecondCharacter(List<ActionType> actions) {
        actionChoice2.getItems().setAll(actions.stream().map(ActionType::toString).toList());
        actionChoice2.getSelectionModel().selectFirst();
    }

    public int getFirstActionIndex()  { return actionChoice1.getSelectionModel().getSelectedIndex(); }
    public int getSecondActionIndex() { return actionChoice2.getSelectionModel().getSelectedIndex(); }

    // Image personnage

    private void updateCharacterImage(ImageView view, String name) {
        if (name == null) return;
        String path = "/images/characters/" + name.replace(" ", "").toLowerCase() + ".png";
        try { view.setImage(new Image(getClass().getResource(path).toExternalForm())); }
        catch (Exception e) { System.out.println("Image introuvable : " + path); }
    }

    // Popups événement

    private void showPopupInternal(String text) {
        eventPopupText.setText(text);
        eventPopup.setVisible(true);
        setControlsDisabled(true);
    }

    public void showProductionPopup(String text) { showPopupInternal(text); }
    public void showEventPopup(String text) {
        displayEvent(text.lines().findFirst().orElse(""));
        showPopupInternal(text);
    }
    public void showCrisisPopup(String text) { showPopupInternal(text); }

    public void hideEventPopup() {
        eventPopup.setVisible(false);
        setControlsDisabled(false);
    }

    private void setControlsDisabled(boolean disabled) {
        validateButton.setDisable(disabled);
        characterChoice1.setDisable(disabled);
        characterChoice2.setDisable(disabled);
        actionChoice1.setDisable(disabled);
        actionChoice2.setDisable(disabled);
    }

    @FXML private void handleContinue() { }
    @FXML private void handleValidate() { }

    // Menu in-game
    @FXML
    private void showShop() {
        try {
            ingameMenuOverlay.setVisible(false);
            gamePane.setEffect(null);
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/vue/ShopView.fxml"));
            javafx.scene.Parent shopRoot = loader.load();
            ShopViewController shopCtrl = loader.getController();
            shopCtrl.setOnClose(() -> {
                root.getChildren().remove(shopRoot);
                SaveManager.savePurchases(modele.ShopManager.getPurchasedIds());
                if (onShopPurchased != null) onShopPurchased.run();
                if (currentVillage != null)
                    displayCharacters(currentVillage.getCharacters());
            });
            root.getChildren().add(shopRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void toggleIngameMenu() {
        boolean open = !ingameMenuOverlay.isVisible();
        ingameMenuOverlay.setVisible(open);
        if (open) {
            saveOverlay.setVisible(false);
            recapOverlay.setVisible(false);
            ingameSettingsPanel.setVisible(false);
            ingameRulesPanel.setVisible(false);
            gamePane.setEffect(new GaussianBlur(8));
        } else {
            gamePane.setEffect(null);
        }
    }

    @FXML
    private void quitToHome() {
        ingameMenuOverlay.setVisible(false);
        gamePane.setEffect(null);
        if (onQuitToHome != null) onQuitToHome.run();
    }

    @FXML
    private void showIngameSettings() {
        ingameMenuOverlay.setVisible(false);
        ingameRulesPanel.setVisible(false);
        gamePane.setEffect(new GaussianBlur(8));
        ingameSettingsPanel.setVisible(true);
    }

    @FXML
    private void hideIngameSettings() {
        ingameSettingsPanel.setVisible(false);
        gamePane.setEffect(null);
    }

    @FXML
    private void showIngameRules() {
        ingameMenuOverlay.setVisible(false);
        ingameSettingsPanel.setVisible(false);
        currentRulePage = 0;
        updateIngameRulesPage();
        gamePane.setEffect(new GaussianBlur(8));
        ingameRulesPanel.setVisible(true);
    }

    @FXML
    private void hideIngameRules() {
        ingameRulesPanel.setVisible(false);
        gamePane.setEffect(null);
    }

    @FXML private void ingameRulesNext() {
        if (currentRulePage < rulePages.size() - 1) { currentRulePage++; updateIngameRulesPage(); }
    }
    @FXML private void ingameRulesPrev() {
        if (currentRulePage > 0) { currentRulePage--; updateIngameRulesPage(); }
    }

    private void updateIngameRulesPage() {
        RulePage page = rulePages.get(currentRulePage);
        ingameRulesTitle.setText(page.title());
        ingameRulesText.setText(page.text());
        if (page.imagePath() == null) {
            ingameRulesCharImage.setImage(null);
            ingameRulesCharImage.setFitWidth(0);
        } else {
            ingameRulesCharImage.setFitWidth(180);
            try {
                ingameRulesCharImage.setImage(
                    new Image(getClass().getResource(page.imagePath()).toExternalForm()));
            } catch (Exception ignored) {}
        }
        ingameRulesPrevBtn.setVisible(currentRulePage > 0);
        ingameRulesNextBtn.setVisible(currentRulePage < rulePages.size() - 1);
    }

    private void createRulePages() {
        rulePages = new ArrayList<>();

        rulePages.add(new RulePage("Règles", null,
                "Le village des Schtroumpfs doit survivre pendant 12 mois.\n\n"
                + "Chaque mois correspond à un tour de jeu.\n\n"
                + "Déroulement d'un tour :\n"
                + "• Production des ressources.\n"
                + "• Apparition d'un événement aléatoire.\n"
                + "• Choix de deux Schtroumpfs du conseil.\n"
                + "• Réalisation des actions.\n"
                + "• Consommation des ressources.\n"
                + "• Vérification des crises.\n\n"
                + "Si 3 crises sont actives en même temps, la partie est perdue."));

        rulePages.add(new RulePage("Schtroumpfette", "/images/characters/schtroumpfette.png",
                "Schtroumpfette aide à maintenir le moral du village.\n\n"
                + "Elle peut négocier avec les villages voisins, apaiser les conflits et organiser une fête.\n\n"
                + "Elle est très utile lorsque le moral devient faible."));

        rulePages.add(new RulePage("Grand Schtroumpf", "/images/characters/grandschtroumpf.png",
                "Le Grand Schtroumpf guide le village avec sagesse.\n\n"
                + "Il peut consulter son grimoire, organiser une réunion ou négocier avec les animaux.\n\n"
                + "Il aide surtout à améliorer le savoir et le moral."));

        rulePages.add(new RulePage("Schtroumpf Bricoleur", "/images/characters/schtroumpfbricoleur.png",
                "Le Schtroumpf Bricoleur protège le village.\n\n"
                + "Il peut réparer les maisons, construire des pièges ou inventer un gadget.\n\n"
                + "Il est important pour la défense du village."));

        rulePages.add(new RulePage("Schtroumpf Gourmand", "/images/characters/schtroumpfgourmand.png",
                "Le Schtroumpf Gourmand aide à gérer la nourriture.\n\n"
                + "Il peut cueillir des baies, organiser un festin ou trouver un champignon rare.\n\n"
                + "Il est utile pour éviter la famine."));

        rulePages.add(new RulePage("Schtroumpf à Lunettes", "/images/characters/schtroumpfàlunettes.png",
                "Le Schtroumpf à Lunettes augmente le savoir du village.\n\n"
                + "Il peut lire un livre, donner un cours ou faire un discours.\n\n"
                + "Il est utile pour améliorer les connaissances du village."));

        rulePages.add(new RulePage("Schtroumpf Farceur", "/images/characters/schtroumpffarceur.png",
                "Le Schtroumpf Farceur adore surprendre les autres Schtroumpfs.\n\n"
                + "Il peut offrir un cadeau piégé, préparer une farce ou fabriquer un feu d'artifice.\n\n"
                + "Il est utile pour remonter le moral du village."));

        rulePages.add(new RulePage("Schtroumpf Mineur", "/images/characters/schtroumpfmineur.png",
                "Le Schtroumpf Mineur explore les tunnels pour récupérer des ressources.\n\n"
                + "Il peut miner de l'or, renforcer les outils ou sécuriser un tunnel.\n\n"
                + "Il est utile pour augmenter les ressources matérielles du village."));

        rulePages.add(new RulePage("Schtroumpf Magicien", "/images/characters/schtroumpfmagicien.png",
                "Le Schtroumpf Magicien manipule les énergies mystiques pour transformer les ressources.\n\n"
                + "Il peut convertir deux ressources en deux autres, selon les besoins du village.\n\n"
                + "Il est utile pour rééquilibrer les stocks et s'adapter aux situations critiques."));
    }

    // Sauvegarde 

    @FXML
    private void showSavePanel() {
        ingameMenuOverlay.setVisible(false);
        gamePane.setEffect(null);
        refreshSaveSlots();
        saveOverlay.setVisible(true);
    }

    @FXML private void hideSavePanel() { saveOverlay.setVisible(false); }

    @FXML private void saveSlot1() { doSave(1); }
    @FXML private void saveSlot2() { doSave(2); }
    @FXML private void saveSlot3() { doSave(3); }
    @FXML private void saveSlot4() { doSave(4); }

    private void doSave(int slot) {
        if (currentVillage == null) return;
        SaveManager.save(slot, currentVillage, currentVillageName, currentMonth);
        refreshSaveSlots();
        displayMessage("Partie sauvegardée dans le slot " + slot + ".");
        saveOverlay.setVisible(false);
    }

    private void refreshSaveSlots() {
        SaveManager.SlotData[] slots = SaveManager.loadAllSlots();
        refreshSlotBtn(saveSlot1, slots[0], 1);
        refreshSlotBtn(saveSlot2, slots[1], 2);
        refreshSlotBtn(saveSlot3, slots[2], 3);
        refreshSlotBtn(saveSlot4, slots[3], 4);
    }

    private void refreshSlotBtn(Button btn, SaveManager.SlotData data, int n) {
        if (data == null) {
            btn.setText("Slot " + n + "  —  Emplacement vide");
            btn.getStyleClass().removeAll("slot-button-filled");
            if (!btn.getStyleClass().contains("slot-button-empty"))
                btn.getStyleClass().add("slot-button-empty");
        } else {
            btn.setText(data.toSlotLabel(n));
            btn.getStyleClass().removeAll("slot-button-empty");
            if (!btn.getStyleClass().contains("slot-button-filled"))
                btn.getStyleClass().add("slot-button-filled");
        }
    }

    // Récapitulatif

    public void addTurnRecord(TurnRecord record) { turnHistory.add(record); }

    @FXML
    public void toggleRecap() {
        boolean open = !recapOverlay.isVisible();
        if (open) {
            ingameMenuOverlay.setVisible(false);
            buildRecapUI();
            recapOverlay.toFront();
        }
        recapOverlay.setVisible(open);
    }

    private void buildRecapUI() {
        recapContent.getChildren().clear();
        if (turnHistory.isEmpty()) {
            Label empty = new Label("Aucun tour terminé pour l'instant.");
            empty.getStyleClass().add("recap-row-text");
            recapContent.getChildren().add(empty);
            return;
        }
        for (TurnRecord rec : turnHistory) {
            Label lbl = new Label(rec.toDisplayText());
            lbl.getStyleClass().add("recap-row-text");
            lbl.setWrapText(true);
            recapContent.getChildren().add(lbl);
            recapContent.getChildren().add(new Separator());
        }
    }

    public void showEndScreen(boolean victory, int score) {
        endScreenTitle.setText(victory ? "🏆 Victoire du Village !" : "💀 Défaite...");
        endScreenTitle.getStyleClass().removeAll("end-screen-title-victory", "end-screen-title-defeat");
        endScreenTitle.getStyleClass().add(victory ? "end-screen-title-victory" : "end-screen-title-defeat");
        endScreenScore.setText("Score final : " + score);
        endScreenOverlay.setVisible(true);
    }

    // Daltonisme in-game

    @FXML private void cbNone() { applyColorBlind(ColorBlindManager.ColorBlindType.NONE); }
    @FXML private void cbDeuteran() { applyColorBlind(ColorBlindManager.ColorBlindType.DEUTERANOPIA); }
    @FXML private void cbProtan() { applyColorBlind(ColorBlindManager.ColorBlindType.PROTANOPIA); }
    @FXML private void cbTritan() { applyColorBlind(ColorBlindManager.ColorBlindType.TRITANOPIA); }
    @FXML private void cbAchromat() { applyColorBlind(ColorBlindManager.ColorBlindType.ACHROMATOPSIA); }

    private void applyColorBlind(ColorBlindManager.ColorBlindType type) {
        ColorBlindManager.apply(root, type);
        updateColorBlindButtons(type);
    }

    private void updateColorBlindButtons(ColorBlindManager.ColorBlindType active) {
        Map<ColorBlindManager.ColorBlindType, Button> map = new EnumMap<>(ColorBlindManager.ColorBlindType.class);
        map.put(ColorBlindManager.ColorBlindType.NONE,          cbNone);
        map.put(ColorBlindManager.ColorBlindType.DEUTERANOPIA,  cbDeuter);
        map.put(ColorBlindManager.ColorBlindType.PROTANOPIA,    cbProtan);
        map.put(ColorBlindManager.ColorBlindType.TRITANOPIA,    cbTritan);
        map.put(ColorBlindManager.ColorBlindType.ACHROMATOPSIA, cbAchro);
        map.forEach((type, btn) -> {
            btn.getStyleClass().removeAll("colorblind-btn", "colorblind-btn-active");
            btn.getStyleClass().add(type == active ? "colorblind-btn-active" : "colorblind-btn");
        });
    }

    // Plein écran 

    @FXML private void setFullScreenMode() { getStage().setFullScreen(true); }
    @FXML private void setWindowedMode() {
        javafx.stage.Stage s = getStage();
        s.setFullScreen(false);
        s.setWidth(1500); s.setHeight(850); s.centerOnScreen();
    }

    private javafx.stage.Stage getStage() { return (javafx.stage.Stage) root.getScene().getWindow(); }

    // Getters pour JavaFXController

    public ChoiceBox<String> getCharacterChoice1() { return characterChoice1; }
    public ChoiceBox<String> getCharacterChoice2() { return characterChoice2; }
    public ChoiceBox<String> getActionChoice1() { return actionChoice1; }
    public ChoiceBox<String> getActionChoice2() { return actionChoice2; }
    public Button getValidateButton() { return validateButton; }
    public Button getContinueButton() { return continueButton; }

    private record RulePage(String title, String imagePath, String text) {}
}
