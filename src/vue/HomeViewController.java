package vue;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modele.SaveManager;
import vue.ShopViewController;

import java.util.*;

public class HomeViewController {

    // Racines
    @FXML private StackPane  root;
    @FXML private BorderPane homePane;
    @FXML private BorderPane playPane;
    @FXML private BorderPane villageNamePane;

    // Paramètres
    @FXML private VBox   settingsPanel;
    @FXML private Button cbNone;
    @FXML private Button cbDeuter;
    @FXML private Button cbProtan;
    @FXML private Button cbTritan;
    @FXML private Button cbAchro;
    @FXML private Slider volumeSlider;
    @FXML private Slider sfxSlider;

    // Règles
    @FXML private StackPane rulesPanel;
    @FXML private Label     rulesTitle;
    @FXML private Label     rulesText;
    @FXML private ImageView characterImage;
    @FXML private Button    previousPageButton;
    @FXML private Button    nextPageButton;

    // Saisie nom
    @FXML private TextField villageNameField;
    @FXML private Button    enterVillageButton;

    // Chargement (4 slots)
    @FXML private StackPane loadOverlay;
    @FXML private Button    loadSlot1;
    @FXML private Button    loadSlot2;
    @FXML private Button    loadSlot3;
    @FXML private Button    loadSlot4;

    private java.util.function.Consumer<SaveManager.SlotData> onLoadGame;

    // Scoreboard
    @FXML private StackPane scoreboardOverlay;
    @FXML private VBox      scoreboardContent;

    // Règles internes
    private List<RulePage> rulePages;
    private int currentRulePage;


    @FXML
    public void initialize() {
        createRulePages();
        updateRulesPage();
        loadOverlay.setVisible(false);
        scoreboardOverlay.setVisible(false);
        settingsPanel.setVisible(false);
        rulesPanel.setVisible(false);
        
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

    }

    public void setOnLoadGame(java.util.function.Consumer<SaveManager.SlotData> cb) {
        onLoadGame = cb;
    }

    public void syncColorBlindButtons(ColorBlindManager.ColorBlindType active) {
        updateColorBlindButtons(active);
    }

    // Navigation

    @FXML private void showPlayScreen()        { homePane.setVisible(false); villageNamePane.setVisible(false); playPane.setVisible(true);        hideAllPopups(); }
    @FXML private void showVillageNameScreen() { homePane.setVisible(false); playPane.setVisible(false);        villageNamePane.setVisible(true); hideAllPopups(); }
    @FXML private void showHomeScreen()        { playPane.setVisible(false); villageNamePane.setVisible(false); homePane.setVisible(true);        hideAllPopups(); }
    @FXML private void quitApp() { javafx.application.Platform.exit();
    }
    // Paramètres 

    @FXML
    private void showSettings() {
        blurCurrentBackground();
        settingsPanel.setVisible(true);
        rulesPanel.setVisible(false);
        loadOverlay.setVisible(false);
        scoreboardOverlay.setVisible(false);
    }

    @FXML
    private void hideSettings() {
        settingsPanel.setVisible(false);
        clearBlur();
    }

    @FXML private void setFullScreenMode() { getStage().setFullScreen(true); }
    @FXML private void setWindowedMode() {
        Stage s = getStage();
        s.setFullScreen(false);
        s.setWidth(1500); s.setHeight(850); s.centerOnScreen();
    }

    // Daltonisme

    @FXML private void cbNone()     { applyColorBlind(ColorBlindManager.ColorBlindType.NONE); }
    @FXML private void cbDeuteran() { applyColorBlind(ColorBlindManager.ColorBlindType.DEUTERANOPIA); }
    @FXML private void cbProtan()   { applyColorBlind(ColorBlindManager.ColorBlindType.PROTANOPIA); }
    @FXML private void cbTritan()   { applyColorBlind(ColorBlindManager.ColorBlindType.TRITANOPIA); }
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

    // Règles

    @FXML private void showRules() {
        currentRulePage = 0; updateRulesPage();
        blurCurrentBackground();
        rulesPanel.setVisible(true);
        settingsPanel.setVisible(false);
    }
    @FXML private void hideRules()         { rulesPanel.setVisible(false); clearBlur(); }
    @FXML private void nextRulePage()      { if (currentRulePage < rulePages.size()-1) { currentRulePage++; updateRulesPage(); } }
    @FXML private void previousRulePage()  { if (currentRulePage > 0) { currentRulePage--; updateRulesPage(); } }

    private void updateRulesPage() {
        RulePage page = rulePages.get(currentRulePage);
        rulesTitle.setText(page.title());
        rulesText.setText(page.text());
        if (page.imagePath() == null) {
            characterImage.setImage(null); characterImage.setFitWidth(0);
        } else {
            characterImage.setFitWidth(210);
            characterImage.setImage(new Image(getClass().getResource(page.imagePath()).toExternalForm()));
        }
        previousPageButton.setVisible(currentRulePage > 0);
        nextPageButton.setVisible(currentRulePage < rulePages.size() - 1);
    }

    private void createRulePages() {
        rulePages = new ArrayList<>();

        rulePages.add(new RulePage(
                "Règles",
                null,
                "Le village des Schtroumpfs doit survivre pendant 12 mois.\n\n"
                        + "Chaque mois correspond à un tour de jeu.\n\n"
                        + "Déroulement d'un tour :\n"
                        + "• Production des ressources.\n"
                        + "• Apparition d'un événement aléatoire.\n"
                        + "• Choix de deux Schtroumpfs du conseil.\n"
                        + "• Réalisation des actions.\n"
                        + "• Consommation des ressources.\n"
                        + "• Vérification des crises.\n\n"
                        + "Si 3 crises sont actives en même temps, la partie est perdue."
        ));

        rulePages.add(new RulePage(
                "Schtroumpfette",
                "/images/characters/schtroumpfette.png",
                "Schtroumpfette aide à maintenir le moral du village.\n\n"
                        + "Elle peut négocier avec les villages voisins, apaiser les conflits et organiser une fête.\n\n"
                        + "Elle est très utile lorsque le moral devient faible."
        ));

        rulePages.add(new RulePage(
                "Grand Schtroumpf",
                "/images/characters/grandschtroumpf.png",
                "Le Grand Schtroumpf guide le village avec sagesse.\n\n"
                        + "Il peut consulter son grimoire, organiser une réunion ou négocier avec les animaux.\n\n"
                        + "Il aide surtout à améliorer le savoir et le moral."
        ));

        rulePages.add(new RulePage(
                "Schtroumpf Bricoleur",
                "/images/characters/schtroumpfbricoleur.png",
                "Le Schtroumpf Bricoleur protège le village.\n\n"
                        + "Il peut réparer les maisons, construire des pièges ou inventer un gadget.\n\n"
                        + "Il est important pour la défense du village."
        ));

        rulePages.add(new RulePage(
                "Schtroumpf Gourmand",
                "/images/characters/schtroumpfgourmand.png",
                "Le Schtroumpf Gourmand aide à gérer la nourriture.\n\n"
                        + "Il peut cueillir des baies, organiser un festin ou trouver un champignon rare.\n\n"
                        + "Il est utile pour éviter la famine."
        ));

        rulePages.add(new RulePage(
                "Schtroumpf à Lunettes",
                "/images/characters/schtroumpfàlunettes.png",
                "Le Schtroumpf à Lunettes augmente le savoir du village.\n\n"
                        + "Il peut lire un livre, donner un cours ou faire un discours.\n\n"
                        + "Il est utile pour améliorer les connaissances du village."
        ));

        rulePages.add(new RulePage(
                "Schtroumpf Farceur",
                "/images/characters/schtroumpffarceur.png",
                "Le Schtroumpf Farceur adore surprendre les autres Schtroumpfs.\n\n"
                        + "Il peut offrir un cadeau piégé, préparer une farce ou fabriquer un feu d'artifice.\n\n"
                        + "Il est utile pour remonter le moral du village."
        ));

        rulePages.add(new RulePage(
                "Schtroumpf Mineur",
                "/images/characters/schtroumpfmineur.png",
                "Le Schtroumpf Mineur explore les tunnels pour récupérer des ressources.\n\n"
                        + "Il peut miner de l'or, renforcer les outils ou sécuriser un tunnel.\n\n"
                        + "Il est utile pour augmenter les ressources matérielles du village."
        ));

        rulePages.add(new RulePage(
                "Schtroumpf Magicien",
                "/images/characters/schtroumpfmagicien.png",
                "Le Schtroumpf Magicien manipule les énergies mystiques pour transformer les ressources.\n\n"
                        + "Il peut convertir deux ressources en deux autres, selon les besoins du village.\n\n"
                        + "Il est utile pour rééquilibrer les stocks et s'adapter aux situations critiques."
        ));
    }

    // Chargement

    @FXML
    private void showLoadPanel() {
        refreshLoadSlots();
        loadOverlay.setVisible(true);
    }

    @FXML private void hideLoadPanel() { loadOverlay.setVisible(false); }

    @FXML private void loadSlot1() { doLoad(1); }
    @FXML private void loadSlot2() { doLoad(2); }
    @FXML private void loadSlot3() { doLoad(3); }
    @FXML private void loadSlot4() { doLoad(4); }

    private void doLoad(int slot) {
        SaveManager.SlotData data = SaveManager.loadSlot(slot);
        if (data == null) return;
        loadOverlay.setVisible(false);
        if (onLoadGame != null) onLoadGame.accept(data);
    }

    private void refreshLoadSlots() {
        SaveManager.SlotData[] slots = SaveManager.loadAllSlots();
        refreshSlotBtn(loadSlot1, slots[0], 1);
        refreshSlotBtn(loadSlot2, slots[1], 2);
        refreshSlotBtn(loadSlot3, slots[2], 3);
        refreshSlotBtn(loadSlot4, slots[3], 4);
    }

    private void refreshSlotBtn(Button btn, SaveManager.SlotData data, int n) {
        if (data == null) {
            btn.setText("Slot " + n + "  —  Emplacement vide");
            btn.getStyleClass().removeAll("slot-button-filled");
            if (!btn.getStyleClass().contains("slot-button-empty"))
                btn.getStyleClass().add("slot-button-empty");
            btn.setDisable(true);
        } else {
            btn.setText(data.toSlotLabel(n));
            btn.getStyleClass().removeAll("slot-button-empty");
            if (!btn.getStyleClass().contains("slot-button-filled"))
                btn.getStyleClass().add("slot-button-filled");
            btn.setDisable(false);
        }
    }

    // Scoreboard

    @FXML
    private void showScoreboard() {
        refreshScoreboard();
        scoreboardOverlay.setVisible(true);
    }

    @FXML private void hideScoreboard() { scoreboardOverlay.setVisible(false); }

    @FXML
    private void clearScoreboard() {
        SaveManager.clearScoreboard();
        refreshScoreboard();
    }

    private void refreshScoreboard() {
        scoreboardContent.getChildren().clear();
        List<SaveManager.ScoreEntry> entries = SaveManager.loadScoreboard();
        if (entries.isEmpty()) {
            Label empty = new Label("Aucun score enregistré.");
            empty.getStyleClass().add("scoreboard-row");
            scoreboardContent.getChildren().add(empty);
            return;
        }
        String[] medals = { "🥇", "🥈", "🥉" };
        for (int i = 0; i < entries.size(); i++) {
            SaveManager.ScoreEntry e = entries.get(i);
            String medal = i < 3 ? medals[i] : (i + 1) + ".";
            HBox row = new HBox();
            row.setSpacing(0);
            Label lMedal = new Label(medal);                    lMedal.setMinWidth(40);
            Label lName  = new Label(e.villageName());          lName.setMinWidth(200);
            Label lScore = new Label(String.valueOf(e.score())); lScore.setMinWidth(100);
            Label lTurns = new Label(e.turns() + " tours");     lTurns.setMinWidth(80);
            Label lDate  = new Label(e.date());                 lDate.setMinWidth(160);
            String style = i == 0 ? "scoreboard-row-gold"
                         : i == 1 ? "scoreboard-row-silver"
                         : i == 2 ? "scoreboard-row-bronze"
                         : "scoreboard-row";
            for (Label l : new Label[]{lMedal, lName, lScore, lTurns, lDate})
                l.getStyleClass().add(style);
            row.getChildren().addAll(lMedal, lName, lScore, lTurns, lDate);
            scoreboardContent.getChildren().add(row);
        }
    }

    //  Boutique

    @FXML
    private void showShop() {
        try {
            blurCurrentBackground();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/vue/ShopView.fxml"));
            javafx.scene.Parent shopRoot = loader.load();
            ShopViewController shopCtrl = loader.getController();
            shopCtrl.setOnClose(() -> {
                root.getChildren().remove(shopRoot);
                modele.SaveManager.savePurchases(modele.ShopManager.getPurchasedIds());
                clearBlur();
            });
            root.getChildren().add(shopRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void blurCurrentBackground() {
        clearBlur();
        if (villageNamePane.isVisible()) villageNamePane.setEffect(new GaussianBlur(8));
        else if (playPane.isVisible())   playPane.setEffect(new GaussianBlur(8));
        else                             homePane.setEffect(new GaussianBlur(8));
    }

    private void clearBlur() {
        homePane.setEffect(null);
        playPane.setEffect(null);
        villageNamePane.setEffect(null);
    }

    private void hideAllPopups() {
        settingsPanel.setVisible(false);
        rulesPanel.setVisible(false);
        loadOverlay.setVisible(false);
        scoreboardOverlay.setVisible(false);
        clearBlur();
    }

    private Stage getStage() { return (Stage) root.getScene().getWindow(); }

    // Getters pour MainApp

    public Parent getRoot()               { return root; }
    public Button getEnterVillageButton() { return enterVillageButton; }
    public String getVillageName()        { return villageNameField.getText(); }

    private record RulePage(String title, String imagePath, String text) {}
}
