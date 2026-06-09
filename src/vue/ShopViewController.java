package vue;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import modele.ShopManager;
import modele.ShopManager.ShopItem;
import modele.ShopManager.Bundle;

public class ShopViewController {

    @FXML private VBox  itemsBox;
    @FXML private VBox  bundleBox;
    @FXML private Label totalLabel;

    private Runnable onClose;

    public void setOnClose(Runnable r) { onClose = r; }

    @FXML
    public void initialize() {
        refreshShop();
    }

    private void refreshShop() {
        itemsBox.getChildren().clear();
        bundleBox.getChildren().clear();

        for (ShopItem item : ShopManager.getItems()) {
            itemsBox.getChildren().add(buildItemCard(item));
        }

        for (Bundle bundle : ShopManager.getBundles()) {
            bundleBox.getChildren().add(buildBundleCard(bundle));
        }
    }

    private HBox buildItemCard(ShopItem item) {
        HBox card = new HBox(16);
        card.getStyleClass().add("shop-card");

        //  Image du schtroumpf 
        ImageView img = new ImageView();
        img.setFitWidth(80);
        img.setFitHeight(80);
        img.setPreserveRatio(true);
        String imgPath = getCharacterImagePath(item.id);
        try {
            img.setImage(new Image(getClass().getResource(imgPath).toExternalForm()));
        } catch (Exception e) {
            System.out.println("Image introuvable : " + imgPath);
        }

        // Infos
        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(item.name);
        name.getStyleClass().add("shop-item-name");

        Label desc = new Label(item.description);
        desc.getStyleClass().add("shop-item-desc");
        desc.setWrapText(true);

        info.getChildren().addAll(name, desc);

        //  Prix + bouton 
        VBox priceBox = new VBox(8);
        priceBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label price = new Label(String.format("%.2f €", item.price));
        price.getStyleClass().add("shop-item-price");

        Button btn;
        if (item.isPurchased()) {
            btn = new Button("✅ Déjà acheté");
            btn.setDisable(true);
            btn.getStyleClass().add("shop-btn-owned");
        } else {
            btn = new Button("Acheter");
            btn.getStyleClass().add("shop-btn-buy");
            btn.setOnAction(e -> showPaymentDialog(item.id, item.name, item.price, false));
        }

        priceBox.getChildren().addAll(price, btn);
        card.getChildren().addAll(img, info, priceBox);
        return card;
    }

    private VBox buildBundleCard(Bundle bundle) {
        VBox card = new VBox(12);
        card.getStyleClass().add("shop-card-bundle");

        //  Titre 
        Label name = new Label("🎁 " + bundle.name);
        name.getStyleClass().add("shop-bundle-name");

        //  Images des 3 schtroumpfs côte à côte 
        HBox imagesRow = new HBox(12);
        imagesRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        for (String id : bundle.itemIds) {
            VBox charBox = new VBox(4);
            charBox.setAlignment(javafx.geometry.Pos.CENTER);

            ImageView img = new ImageView();
            img.setFitWidth(60);
            img.setFitHeight(60);
            img.setPreserveRatio(true);
            try {
                img.setImage(new Image(getClass().getResource(getCharacterImagePath(id)).toExternalForm()));
            } catch (Exception ignored) {}

            ShopItem item = ShopManager.getItemById(id);
            Label charName = new Label(item != null ? item.name : id);
            charName.getStyleClass().add("shop-item-desc");

            charBox.getChildren().addAll(img, charName);
            imagesRow.getChildren().add(charBox);
        }

        //  Description 
        Label desc = new Label(bundle.description);
        desc.getStyleClass().add("shop-item-desc");
        desc.setWrapText(true);

        //  Prix 
        HBox priceRow = new HBox(20);
        priceRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        double normalPrice = ShopManager.getTotalSeparate();
        Label oldPrice = new Label(String.format("Prix normal : %.2f €", normalPrice));
        oldPrice.getStyleClass().add("shop-old-price");

        Label price = new Label(String.format("Pack : %.2f €", bundle.price));
        price.getStyleClass().add("shop-bundle-price");

        double savings = normalPrice - bundle.price;
        Label saving = new Label(String.format("🏷 Économie : %.2f €", savings));
        saving.getStyleClass().add("shop-item-desc");

        priceRow.getChildren().addAll(oldPrice, price, saving);

        //  Bouton 
        Button btn;
        if (bundle.isPurchased()) {
            btn = new Button("✅ Pack déjà acheté");
            btn.setDisable(true);
            btn.getStyleClass().add("shop-btn-owned");
        } else {
            btn = new Button(" Acheter le pack — " + String.format("%.2f €", bundle.price));
            btn.getStyleClass().add("shop-btn-bundle");
            btn.setOnAction(e -> showPaymentDialog(bundle.id, bundle.name, bundle.price, true));
        }

        card.getChildren().addAll(name, imagesRow, desc, priceRow, btn);
        return card;
    }

    private String getCharacterImagePath(String id) {
        return switch (id) {
            case "BRAINY"   -> "/images/characters/schtroumpfàlunettes.png";
            case "MINER"    -> "/images/characters/schtroumpfmineur.png";
            case "MAGICIAN" -> "/images/characters/schtroumpfmagicien.png";
            default         -> "/images/characters/grandschtroumpf.png";
        };
    }

    private void showPaymentDialog(String id, String name, double price, boolean isBundle) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Paiement simulé");
        dialog.setHeaderText("Achat : " + name + " — " + String.format("%.2f €", price));

        VBox content = new VBox(12);
        content.getStyleClass().add("payment-form");

        Label cardLabel = new Label("Numéro de carte");
        cardLabel.getStyleClass().add("payment-label");
        TextField cardField = new TextField();
        cardField.setPromptText("1234 5678 9012 3456");
        cardField.getStyleClass().add("payment-field");

        Label expiryLabel = new Label("Date d'expiration");
        expiryLabel.getStyleClass().add("payment-label");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/AA");
        expiryField.getStyleClass().add("payment-field");

        Label cvvLabel = new Label("CVV");
        cvvLabel.getStyleClass().add("payment-label");
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");
        cvvField.getStyleClass().add("payment-field");

        Label disclaimer = new Label("Paiement simulé");
        disclaimer.getStyleClass().add("payment-disclaimer");
        disclaimer.setWrapText(true);

        content.getChildren().addAll(cardLabel, cardField, expiryLabel, expiryField,
                                     cvvLabel, cvvField, disclaimer);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Confirmer l'achat");

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (!validateCard(cardField.getText(), expiryField.getText(), cvvField.getText())) {
                    showError("Informations de carte invalides.\nVérifiez vos données.");
                    return;
                }
                if (isBundle) ShopManager.purchaseBundle(id);
                else          ShopManager.purchase(id);
                showSuccess(name);
                refreshShop();
            }
        });
    }

    private boolean validateCard(String card, String expiry, String cvv) {
        card = card.replaceAll("\\s", "");
        return card.matches("\\d{16}")
            && expiry.matches("\\d{2}/\\d{2}")
            && cvv.matches("\\d{3}");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de paiement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Achat confirmé !");
        alert.setHeaderText(null);
        alert.setContentText("✅ " + name + " a été débloqué !\nBonne chance dans votre village.");
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        if (onClose != null) onClose.run();
    }
}
