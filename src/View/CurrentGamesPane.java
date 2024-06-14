package View;

import Controller.ApplicationController;
import Controller.LobbyController;
import Model.Game;
import Model.PlayStatus;
import Model.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class CurrentGamesPane extends Pane {
    private VBox layout;
    private ScrollPane scrollPane;
    private double newHeight;
    private LobbyController lobbyController;

    public CurrentGamesPane(double newHeight, LobbyController lobbyController) {
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

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(ApplicationController.SPACING);

        Label gamesOverviewLabel = new Label("Huidige spellen");
        gamesOverviewLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZEMEDIUM));
        gamesOverviewLabel.setTextFill(Color.WHITE);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshGames());
        HBox.setHgrow(gamesOverviewLabel, Priority.ALWAYS);

        header.getChildren().addAll(gamesOverviewLabel, refreshButton);
        layout.getChildren().add(header);

        displayGames();
        this.getChildren().add(scrollPane);
    }

    public void displayGames() {
        try {

            String currentUsername = ApplicationController.currentLoggedInAccount.getUsername().toLowerCase();

            layout.getChildren().clear();
            layout.getChildren().add(createHeader());

//            String currentUsername = ApplicationController.currentLoggedInAccount.getUsername();

            List<Game> games = lobbyController.getAllGames();
            List<Player> players = lobbyController.getAllPlayers();
            for (Game game : games) {
                for (Player player : players) {
                    if (player != null && currentUsername != null && game != null) {
                        if (player.getIdGame() == game.getId() &&
                                Objects.equals(player.getUsername(), currentUsername) &&
                                (player.getPlayStatus().equals(PlayStatus.ACCEPTED.toString()) ||
                                        player.getPlayStatus().equals(PlayStatus.CHALLENGER.toString()) ||
                                        player.getPlayStatus().equals(PlayStatus.FINISHED.toString()))) {
                            layout.getChildren().add(createGameCard(game));
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public HBox getActionButtons(Game game) {
        Button joinButton = new Button("Deelnemen");
        joinButton.setOnAction(e -> {
            try {
                lobbyController.setSelectedGameId(game.getId(), lobbyController.getPlayer(game.getId()));
                lobbyController.checkJoinPossibility(lobbyController.getPlayer(game.getId()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return new HBox(ApplicationController.SPACING, joinButton);
    }

    public void adjustHeight(double height) {
        if (scrollPane != null) {
            scrollPane.setPrefHeight(height);
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
        Label roundIdLabel = new Label("Huidige Ronde: " + game.getCurrentRoundNr());
        roundIdLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        Label dateLabel = new Label(String.format("Aanmaak Datum: " + game.getCreationDate(), (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        HBox buttonPane = getActionButtons(game);
        card.getChildren().addAll(idLabel, turnPlayerLabel, roundIdLabel, dateLabel, buttonPane);
        return card;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(ApplicationController.SPACING);

        Label gamesOverviewLabel = new Label("Huidige spellen");
        gamesOverviewLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZEMEDIUM));
        gamesOverviewLabel.setTextFill(Color.WHITE);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshGames());
        HBox.setHgrow(gamesOverviewLabel, Priority.ALWAYS);

        header.getChildren().addAll(gamesOverviewLabel, refreshButton);
        return header;
    }
}
