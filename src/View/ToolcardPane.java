package View;

import Controller.GameController;
import Controller.ToolcardController;
import Model.Toolcard;
import Utilities.ImageStore;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

//TODO toolcards weergeven, dit is puur de container die 3 per game weergeeft en de controller aanroept bij actie.
public class ToolcardPane extends HBox {

    private final List<Toolcard> gameToolCards;
    private final GameController gameController;
    private final ToolcardController toolCardController;
    private final ImageStore imageStore = new ImageStore();

    public ToolcardPane(List<Toolcard> gameToolCards, ToolcardController toolCardController, GameController gameController) {
        this.gameController = gameController;
        this.toolCardController = toolCardController;
        this.gameToolCards = gameToolCards;
        this.setSpacing(5);
        this.setPadding(new Insets(0, 0, 0, 10));
        drawToolcards();
    }

    private void drawToolcards() {
        for (Toolcard toolCard : gameToolCards) {

            ImageView toolCardImage = new ImageView(imageStore.getToolcardImage(toolCard));
            toolCardImage.setFitWidth(110);
            toolCardImage.setFitHeight(260);

            toolCardImage.setStyle("-fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 5;");
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.25));
            dropShadow.setRadius(10);
            dropShadow.setSpread(0.2);
            toolCardImage.setEffect(dropShadow);

            toolCardImage.setOnMouseEntered(e -> {
                toolCardImage.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-border-radius: 5;");
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
                dropShadow.setSpread(0.5);
            });

            toolCardImage.setOnMouseExited(e -> {
                toolCardImage.setStyle("-fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 5;");
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.25));
                dropShadow.setSpread(0.2);
            });

            toolCardImage.setOnMouseClicked(e -> {
                showToolCardDetails(toolCard);
            });

            // Add the VBox to the ToolcardPane
            this.getChildren().add(toolCardImage);
        }
    }

    private void showToolCardDetails(Toolcard toolCard) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle(toolCard.getName());
        detailStage.setResizable(false);
        detailStage.getIcons().add(new Image("Resources/images/tool-icon.png"));

        Scene toolCardDetailScene = new Scene(new ToolcardDetailPane(toolCard, gameController, toolCardController, detailStage));
        detailStage.setScene(toolCardDetailScene);

        detailStage.showAndWait();
    }

}
