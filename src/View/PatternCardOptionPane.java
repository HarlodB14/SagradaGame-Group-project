package View;

import Controller.ApplicationController;
import Controller.GameController;
import Controller.PatternCardController;
import Model.Patterncard;
import Model.Player;
import Utilities.ImageStore;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatternCardOptionPane extends GridPane {

    private ImageStore imageStore;
    private GameController gameController;
    private PatternCardController patternCardController;

    public PatternCardOptionPane(GameController gameController, PatternCardController patternCardController) {
        this.gameController = gameController;
        this.patternCardController = patternCardController;
        this.imageStore = new ImageStore();

        setPaneGap();
        createFourOptions();
    }

    private void setPaneGap() {
        setHgap(10);
        setVgap(10);
        this.setAlignment(Pos.CENTER);
    }

    private void createFourOptions() {
        List<Patterncard> patternCardOptions;
        try {
            Player currentPlayer = gameController.getCurrentPlayer();
            patternCardOptions = new ArrayList<>(patternCardController.getPatternCardOptions(currentPlayer.getIdPlayer(), currentPlayer.getIdGame()));
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        createLabel();

        for (int index = 0; index < patternCardOptions.size(); index++) {
            Patterncard patternCard = patternCardOptions.get(index);
            Pane patterncardPane = imageStore.getPatterncardPane(patternCard.getPatternCardID());

            Button button = new Button("Selecteer");
            button.setOnAction(event -> setSelectedPatterncard(patternCard.getPatternCardID()));

            VBox patternCardContainer = new VBox(patterncardPane, button);
            patternCardContainer.setAlignment(Pos.CENTER);
            add(patternCardContainer, index, 1);
        }
    }

    private void createLabel() {
        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER);
        Label selectCardLabel = new Label("Kies een patroonkaart");
        selectCardLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZEMEDIUM));
        labelContainer.getChildren().add(selectCardLabel);
        this.add(labelContainer, 0, 0, 4, 1);
    }

    private void setSelectedPatterncard(int patterncardId) {
        gameController.setSelectedPatternCard(patterncardId);
        ((GameScene) this.getScene()).removePatternCardOptions();
        GameController gcNew = null;
        try {
            gcNew = new GameController(gameController.getGame().getId(), gameController.getCurrentPlayer().getIdPlayer());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        GameScene gameScene = new GameScene(gcNew, new PatternCardController(), true);
        ApplicationController.switchScene(gameScene);
    }
}
