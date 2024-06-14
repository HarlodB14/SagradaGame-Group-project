package View;

import Controller.ApplicationController;
import Controller.LobbyController;
import Model.Game;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GameOverviewPane extends Pane {
    private VBox layout;
    private ScrollPane scrollPane;
    private double newHeight;
    private LobbyController lobbyController;

    public GameOverviewPane(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        setPrefSize(ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        initializeLayout();
        this.newHeight = newHeight;
    }

    private void initializeLayout() {
        layout = new VBox(ApplicationController.SPACING);
        layout.setBackground(ApplicationController.BACKGROUNDCOLOR_DARKGREY);
        layout.setPadding(ApplicationController.COMMON_PADDING);
        layout.setMinHeight(Region.USE_PREF_SIZE);

        scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPrefSize(ApplicationController.WINDOW_WIDTH, newHeight);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        layout.getChildren().add(createHeader());
        displayGames();

        this.getChildren().add(scrollPane);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(ApplicationController.SPACING);

        Label gamesOverviewLabel = new Label("Alle spellen");
        gamesOverviewLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZEMEDIUM));
        gamesOverviewLabel.setTextFill(Color.WHITE);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshGames());

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("Sorteer op datum: Oplopend", "Sorteer op datum: Aflopend");
        sortOptions.setPromptText("Sorteer op datum");
        sortOptions.setOnAction(event -> {
            String selectedOption = sortOptions.getValue();
            boolean ascending = selectedOption.equals("Sorteer op datum: Oplopend");
            refreshSortedGames(ascending);
        });

        HBox.setHgrow(gamesOverviewLabel, Priority.ALWAYS);
        header.getChildren().addAll(gamesOverviewLabel, refreshButton, sortOptions);
        return header;
    }

    public void displayGames() {
        try {
            List<Game> games = lobbyController.getAllGames();
            layout.getChildren().clear();
            layout.getChildren().add(createHeader());
            for (Game game : games) {
                if (game != null) {
                    layout.getChildren().add(createGameCard(game));
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void adjustHeight(double height) {
        if (scrollPane != null) {
            scrollPane.setPrefHeight(height);
        }
    }

    private void refreshSortedGames(boolean ascending) {
        try {
            List<Game> games = lobbyController.getGamesSortedByDate(ascending);
            layout.getChildren().clear();
            layout.getChildren().add(createHeader());
            for (Game game : games) {
                layout.getChildren().add(createGameCard(game));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void refreshGames() {

        displayGames();

    }

    private Pane createGameCard(Game game) {
        VBox card = new VBox(ApplicationController.SPACING);
        card.setPadding(ApplicationController.COMMON_PADDING);
        card.setBackground(ApplicationController.CARD_BACKGROUND);
        card.setMaxWidth(ApplicationController.WINDOW_WIDTH * 0.8);

        Label idLabel = new Label("Game ID: " + game.getId());
        idLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        Label turnPlayerLabel = new Label("Beurt Speler: " + game.getTurnIdPlayerName());
        turnPlayerLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        Label roundIdLabel = new Label("Huidige Ronde: " + game.getCurrentRoundId());
        roundIdLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        Label dateLabel = new Label(String.format("Aanmaak Datum: " + game.getCreationDate(), (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));

        card.getChildren().addAll(idLabel, turnPlayerLabel, roundIdLabel, dateLabel);
        return card;
    }
}
