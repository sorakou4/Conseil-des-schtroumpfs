package lanceur;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modele.SaveManager;
import modele.SmurfVillage;
import vue.ColorBlindManager;
import vue.GameViewController;
import vue.HomeViewController;
import vue.MediaManager;
import controleur.JavaFXController;

public class MainApp extends Application {

    private Scene              scene;
    private HomeViewController homeView;
    private Parent             homeRoot;

    @Override
    public void start(Stage stage) {
        try {
            // Charger les achats au démarrage
            SaveManager.loadPurchases();

            FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/vue/HomeView.fxml"));
            homeRoot = homeLoader.load();
            homeView = homeLoader.getController();

            scene = new Scene(homeRoot, 1536, 1024);
            scene.getStylesheets().add(getClass().getResource("/vue/style.css").toExternalForm());

            // Nouvelle partie
            homeView.getEnterVillageButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String villageName = homeView.getVillageName();
                    if (villageName == null || villageName.isBlank()) {
                        villageName = "des Schtroumpfs";
                    }
                    launchGame(new SmurfVillage(), villageName, 1);
                }
            });

            // Charger une partie
            homeView.setOnLoadGame(data -> {
                SmurfVillage village = new SmurfVillage();
                for (var entry : data.resources.entrySet()) {
                    try {
                        modele.Resource r = modele.Resource.valueOf(entry.getKey());
                        village.modifyResource(r, entry.getValue() - 5);
                    } catch (IllegalArgumentException ignored) {}
                }
                launchGame(village, data.villageName, data.turn);
            });

            stage.setTitle("Conseil des Schtroumpfs");
            stage.setScene(scene);
            stage.setFullScreenExitHint("");
            stage.setFullScreen(true);
            stage.show();

            MediaManager.play("/music/home.wav");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchGame(SmurfVillage village, String villageName, int startTurn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vue/GameView.fxml"));
            Parent gameRoot = loader.load();
            GameViewController gameView = loader.getController();

            gameView.setVillageName(villageName);

            ColorBlindManager.ColorBlindType activeFilter = ColorBlindManager.getCurrentType();
            ColorBlindManager.apply((javafx.scene.layout.Region) gameRoot, activeFilter);
            gameView.syncColorBlindButtons(activeFilter);

            gameView.setOnQuitToHome(() -> returnToHome());

            // Passer le callback boutique au GameView
            gameView.setOnShopPurchased(() -> village.refreshCharacters());

            JavaFXController ctrl = new JavaFXController(village, gameView, villageName);
            if (startTurn > 1) {
                ctrl.setCurrentMonth(startTurn);
            }

            scene.setRoot(gameRoot);
            MediaManager.play("/music/game.wav");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnToHome() {
        ColorBlindManager.ColorBlindType activeFilter = ColorBlindManager.getCurrentType();
        scene.setRoot(homeRoot);
        MediaManager.play("/music/home.wav");
        ColorBlindManager.apply((javafx.scene.layout.Region) homeRoot, activeFilter);
        homeView.syncColorBlindButtons(activeFilter);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
