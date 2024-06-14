package View;

import Controller.*;
import Model.Patterncard;
import Utilities.GameSceneRefreshThread;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.util.Timer;

public class GameScene extends Scene {
    private GameController gameController;
    private PatternCardOptionPane patternCardOptions;
    private FavorTokenController favorTokenController;
    private ToolcardController toolcardController;
    private DicePane dicePane;
    private PatternCardPane patternCardPane;
    private Timer refreshTimer;
    private GameSceneRefreshThread gameSceneRefreshThread;

    private final Insets TOP_BAR_PADDING = new Insets(5);
    private final double TOP_BAR_SPACING = ApplicationController.WINDOW_WIDTH - 200;
    private final Font TOP_BAR_FONT = new Font(16);
    private final double BOTTOM_BAR_SPACING = ApplicationController.WINDOW_WIDTH - 400;
    private RoundTrackPane roundTrackPane;

    public GameScene(GameController gameController, PatternCardController patternCardController) {
        super(new StackPane(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        this.gameController = gameController;
        this.patternCardOptions = new PatternCardOptionPane(gameController, patternCardController);
        setPatterncardOptions();
    }

    public GameScene(GameController gameController, PatternCardController patternCardController, boolean isGameScene) {
        super(new BorderPane(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        this.gameController = gameController;
        favorTokenController = new FavorTokenController();

        toolcardController = new ToolcardController(gameController);

        this.roundTrackPane = new RoundTrackPane(gameController.getCurrentPlayer().getIdGame(), new RoundTrackController(gameController));

        configureLayout();
        startRefreshThread();
    }

    private void configureLayout() {
        BorderPane root = (BorderPane) this.getRoot();

        // Center part configuration
        BorderPane centerPane = new BorderPane();
        centerPane.setTop(roundTrackPane);
        centerPane.setCenter(createPatternCardGrid());
        centerPane.setBottom(createDicePane());

        root.setCenter(centerPane);
        root.setLeft(createLeftSidePane());
        root.setRight(createRightSidePane());
        root.setTop(createTopPane());
        root.setBottom(createBottomPane());
    }

    private GridPane createPatternCardGrid() {
        // Patterncard display (assuming you have multiple patterncards)
        GridPane patterncardGrid = new GridPane();
        patterncardGrid.setAlignment(Pos.CENTER);
        patterncardGrid.setHgap(10);  // Horizontal gap between cells
        patterncardGrid.setVgap(10);  // Vertical gap between cells

        int column = 0;
        int row = 0;
        for (Patterncard patterncard : gameController.getPatterncards()) {
            PatternCardPane patterncardPane;
            try {
                patterncardPane = new PatternCardPane(patterncard.getPatternCardID(), patterncard.getPlayerID(), patterncard.getPlayerUsername(), patterncard.getName(), patterncard.getDifficulty(), patterncard.getSquares(), gameController);
                patterncardGrid.add(patterncardPane, column, row);
                column++;  // Move to the next column
                if (column == 2) {  // After placing two patterncards, reset column and increment row
                    column = 0;
                    row++;
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Ideally handle exceptions more gracefully
            }
        }

        return patterncardGrid;
    }

    private void updatePatternCardGrid() {
        BorderPane centerPane = (BorderPane) ((BorderPane) getRoot()).getCenter();
        GridPane newPatternCardGrid = createPatternCardGrid();

        centerPane.setCenter(newPatternCardGrid);
    }

    private void updateDicePane() {
        BorderPane centerPane = (BorderPane) ((BorderPane) getRoot()).getCenter();
        DicePane newDicePane = createDicePane();

        centerPane.setBottom(newDicePane);
    }

    private void refreshRoundTrackPane() {
        roundTrackPane.refreshRoundTrack();
    }

    private DicePane createDicePane() {
        dicePane = new DicePane(gameController.getAvailableDice(), gameController);
        dicePane.setAlignment(Pos.CENTER);
        return dicePane;
    }

    private Pane createTopPane() {
        HBox topPane = new HBox(TOP_BAR_SPACING);
        topPane.setPadding(TOP_BAR_PADDING);
        topPane.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("Terug");
        backButton.setOnAction(e -> {
            stopRefreshThread();
            ApplicationController.switchScene(new LobbyScene(new LobbyController()));
        });
        backButton.setFont(TOP_BAR_FONT);

        Label roundLabel = new Label("Ronde " + gameController.getRoundNr());
        roundLabel.setFont(TOP_BAR_FONT);

        topPane.getChildren().addAll(backButton, roundLabel);

        return topPane;
    }

    private void updateTopPane() {
        try {
            HBox topPane = (HBox) ((BorderPane) getRoot()).getTop();
            Label roundLabel = (Label) topPane.getChildren().get(1);
            roundLabel.setText("Ronde " + gameController.getRoundNr());
        }
        catch (Exception e) {
            System.out.println("Dit is null");
        }

    }

    private Pane createLeftSidePane() {
        VBox sidePane = new VBox(10); // Increased spacing between elements
        sidePane.setPrefWidth(ApplicationController.WINDOW_WIDTH * 0.25);
        sidePane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        ChatPane chatPane = new ChatPane(gameController.getCurrentPlayer().getIdGame());
        chatPane.setPrefHeight(300); // Set a reasonable height for the chat pane
        VBox.setMargin(chatPane, new Insets(10));
        sidePane.getChildren().add(chatPane);

        Label publicObjectiveMessage = new Label("Klik voor de andere doelkaarten");
        VBox.setMargin(publicObjectiveMessage, new Insets(0, 0, 60, 75));

        PublicObjectiveCardPane publicObjectiveCardPane = new PublicObjectiveCardPane(gameController.getPublicObjectiveCardNumbers());
        VBox.setMargin(publicObjectiveCardPane, new Insets(0, 50, 0, 50));

        sidePane.getChildren().add(publicObjectiveCardPane);
        sidePane.getChildren().add(publicObjectiveMessage);

        return sidePane;
    }

    private void updateLeftSidePane() {
        VBox leftSidePane = (VBox) ((BorderPane) getRoot()).getLeft();
        leftSidePane.getChildren().clear();
        leftSidePane.getChildren().add(createLeftSidePane());
    }

    private VBox createRightSidePane() {
        VBox rightSidePane = new VBox(5);
        rightSidePane.setPrefWidth(ApplicationController.WINDOW_WIDTH * 0.25);

        // Add favor token pane to right side of game scene
        rightSidePane.getChildren().add(favorTokenController.generateFavorTokenPane(gameController.getCurrentPlayer().getIdPlayer()));
        // Toolcard komt hier rechtsboven in de hoek
        rightSidePane.getChildren().add(toolcardController.drawToolcards(gameController.getCurrentGame().getId()));

        // Create private objective card pane
        PrivateObjectiveCardPane privateObjectiveCardPane = new PrivateObjectiveCardPane(gameController.getCurrentPlayer().getPrivateObjectiveCardColor());

        // Set alignment of private objective card pane to bottom
        VBox.setMargin(privateObjectiveCardPane, new Insets(2, 0, 0, 0)); // Adjust margin as needed to lower the card pane

        // Add private objective card pane to bottom of right side pane
        rightSidePane.getChildren().add(privateObjectiveCardPane);

        rightSidePane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        return rightSidePane;
    }

    private void updateRightSidePane() {
        VBox rightSidePane = (VBox) ((BorderPane) getRoot()).getRight();
        PrivateObjectiveCardPane privateObjectiveCardPane = null;
        ToolcardPane toolcardPane = null;

        // Find the PrivateObjectiveCardPane and ToolcardPane among the children
        for (Node child : rightSidePane.getChildren()) {
            if (child instanceof PrivateObjectiveCardPane) {
                privateObjectiveCardPane = (PrivateObjectiveCardPane) child;
            }
        }

        if (privateObjectiveCardPane != null) {
            VBox.setMargin(privateObjectiveCardPane, new Insets(2, 0, 0, 0));
        }

        rightSidePane.getChildren().clear();
        rightSidePane.getChildren().add(favorTokenController.generateFavorTokenPane(gameController.getCurrentPlayer().getIdPlayer()));
        rightSidePane.getChildren().add(toolcardController.drawToolcards(gameController.getCurrentGame().getId()));

        if (privateObjectiveCardPane != null) {
            rightSidePane.getChildren().add(privateObjectiveCardPane);
        }

        rightSidePane.getChildren().clear();
        rightSidePane.getChildren().add(createRightSidePane());

    }

    private Pane createBottomPane() {
        HBox bottomPane = new HBox(BOTTOM_BAR_SPACING);
        bottomPane.setPadding(TOP_BAR_PADDING);
        bottomPane.setAlignment(Pos.CENTER_LEFT);

        Button endTurnButton = new Button("Beurt beëindigen");
        endTurnButton.setOnAction(e -> {
            gameController.nextTurn();
        });
        endTurnButton.setFont(TOP_BAR_FONT);

        if (gameController.getCurrentPlayer().getIdPlayer() != gameController.getTurnPlayerId()) {
            endTurnButton.setDisable(true);
        }

        Label roundLabel = new Label("Speler aan de beurt: " + gameController.getNameFromPlayerTurn());
        roundLabel.setFont(TOP_BAR_FONT);

        bottomPane.getChildren().addAll(roundLabel, endTurnButton);

        return bottomPane;
    }

    private void updateBottomPane() {
        HBox bottomPane = (HBox) ((BorderPane) getRoot()).getBottom();
        Label label = (Label) bottomPane.getChildren().get(0);
        Button endTurnButton = (Button) bottomPane.getChildren().get(1);

        endTurnButton.setDisable(gameController.getCurrentPlayer().getIdPlayer() != gameController.getTurnPlayerId());

        label.setText("Speler aan de beurt: " + gameController.getNameFromPlayerTurn());
    }

    private void setPatterncardOptions() {
        StackPane root = (StackPane) this.getRoot();
        root.setAlignment(Pos.CENTER);
        ((Pane) this.getRoot()).getChildren().add(patternCardOptions);
    }

    public void removePatternCardOptions() {
        ((Pane) this.getRoot()).getChildren().remove(patternCardOptions);
    }

    public void startRefreshThread() {
        gameSceneRefreshThread = new GameSceneRefreshThread(gameController, this);
        gameSceneRefreshThread.start();
    }

    public void updateLayout() {
            updateTopPane();
            updateLeftSidePane();
            updateRightSidePane();
            updateBottomPane();
            updatePatternCardGrid();
            updateDicePane();
            refreshRoundTrackPane();
    }

    public void stopRefreshThread() {
        if (gameSceneRefreshThread != null) {
            gameSceneRefreshThread.stop();
        }
    }
}
