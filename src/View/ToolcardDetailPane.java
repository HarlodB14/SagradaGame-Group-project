package View;

import Controller.ApplicationController;
import Controller.GameController;
import Controller.ToolcardController;
import Model.Toolcard;
import Utilities.ImageStore;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;

//TODO: nummer, descriptie en waarde van een specifieke toolcard weergeven via hier. Deze laat alleen de details van een toolcard zien.
public class ToolcardDetailPane extends VBox {

    private final GameController gameController;
    private final ToolcardController toolcardController;
    private final Stage detailStage;
    private final Toolcard toolcard;

    private int number;
    private String name;
    private String description;
    private int cost;

    public ToolcardDetailPane(Toolcard toolcard, GameController gameController, ToolcardController toolcardController, Stage detailStage) {
        super(20);
        this.setPadding(new Insets(10));

        this.gameController = gameController;
        this.detailStage = detailStage;
        this.toolcardController = toolcardController;
        this.toolcard = toolcard;

        this.number = toolcard.getIdToolcard();
        this.name = toolcard.getName();
        this.description = toolcard.getDescription();
        this.cost = toolcard.getCost(gameController.getCurrentGame().getId());

        ImageStore imageStore = new ImageStore();
        ImageView toolCardImage = new ImageView(imageStore.getToolcardImage(toolcard));

        VBox historyPane = toolcardController.getPurchaseHistory(gameController.getCurrentGame().getId(), toolcard.getIdToolcard());
        historyPane.setSpacing(10);

        HBox informationPane = new HBox(toolCardImage, historyPane);
        informationPane.setSpacing(20);

        Label priceLabel = new Label("Prijs: " + toolcard.getCost(gameController.getCurrentGame().getId()));
        priceLabel.setFont(new Font(16));

        this.getChildren().addAll(informationPane, priceLabel, createControlPane());
    }

    private HBox createControlPane() {
        HBox controlPane = new HBox();
        controlPane.setSpacing(300);
        controlPane.setPadding(new Insets(10));

        Button cancelButton = new Button("Terug");
        cancelButton.setOnAction(e -> {
            detailStage.close();
        });

        Button confirmButton = new Button("Kopen");
        confirmButton.setOnAction(e -> {
            try {
                toolcardController.buyToolcard(gameController.getCurrentGame().getId(), toolcard.getIdToolcard(), detailStage);
            } catch (SQLException error) {
                error.printStackTrace();
            }
        });

        if (toolcardController.hasBoughtTools() || gameController.getCurrentPlayer().getIdPlayer() != gameController.getTurnPlayerId()) {
            confirmButton.setDisable(true);
        }

        controlPane.getChildren().addAll(cancelButton, confirmButton);

        return controlPane;
    }
}
