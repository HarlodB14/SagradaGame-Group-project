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
import java.util.ArrayList;
import java.util.List;

public class InvitePane extends Pane {
    private VBox layout;
    private ScrollPane scrollPane;
    private double newHeight;
    private LobbyController lobbyController;

    public InvitePane(double newHeight, LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        this.newHeight = newHeight;
        setPrefSize(ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        initializeLayout();
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

        Label gamesOverviewLabel = new Label("Uitnodigingen");
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
            layout.getChildren().clear();
            layout.getChildren().add(createHeader());

            List<Game> games = lobbyController.getAllGames();
            List<Player> currentPlayers = lobbyController.getAllCurrentPlayers(ApplicationController.currentLoggedInAccount.getUsername());
            for (Player currentPlayer : currentPlayers) {
                for (Game game : games) {
                    if (game != null && currentPlayer != null) {
                        if (game.getId() == currentPlayer.getIdGame() &&
                                !currentPlayer.getPlayStatus().equals(PlayStatus.CHALLENGER.toString()) &&
                                !currentPlayer.getPlayStatus().equals(PlayStatus.ACCEPTED.toString()) &&
                                !currentPlayer.getPlayStatus().equals(PlayStatus.REFUSED.toString()) &&
                                !currentPlayer.getPlayStatus().equals(PlayStatus.FINISHED.toString())) {
                            Pane gameCard = createGameCard(game, currentPlayer);
                            layout.getChildren().add(gameCard);
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void refreshGames() {
        displayGames();
    }

    public void adjustHeight(double height) {
        if (scrollPane != null) {
            scrollPane.setPrefHeight(height);
        }
    }

    public Pane createGameCard(Game game, Player player) {
        VBox card = new VBox(ApplicationController.SPACING);
        card.setPadding(ApplicationController.COMMON_PADDING);
        card.setBackground(ApplicationController.CARD_BACKGROUND);
        card.setMaxWidth(ApplicationController.WINDOW_WIDTH * 0.8);

        List<Label> labels = setCardLabels(game);
        card.getChildren().addAll(labels);
        Label statusLabel = labels.get(0);
        HBox buttonPane = getActionButtons(player, game, statusLabel);
        card.getChildren().add(buttonPane);

        return card;
    }

    private List<Label> setCardLabels(Game game) {
        List<Label> labels = new ArrayList<>();
        Label statusLabel = new Label();
        statusLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        statusLabel.setTextFill(Color.GRAY);
        labels.add(statusLabel);

        Label idLabel = new Label("Game ID: " + game.getId());
        idLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        labels.add(idLabel);

        Label turnPlayerLabel = new Label("Beurt Speler: " + game.getTurnIdPlayerName());
        turnPlayerLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        labels.add(turnPlayerLabel);

        Label roundIdLabel = new Label("Huidige Ronde: " + game.getCurrentRoundNr());
        roundIdLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        labels.add(roundIdLabel);

        Label dateLabel = new Label(String.format("Aanmaak-datum: " + game.getCreationDate(), (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        labels.add(dateLabel);

        return labels;
    }

    public HBox getActionButtons(Player player, Game game, Label statusLabel) {
        Button acceptButton = new Button("Accepteren");
        Button declineButton = new Button("Weigeren");
        try {
            Player currentPlayer = lobbyController.getPlayer(game.getId());
            statusLabel.setText("Status: " + currentPlayer.getPlayStatus());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setButtonVisibility(player, game, statusLabel, acceptButton, declineButton);
        handleButtonAction(player, statusLabel, acceptButton, declineButton);

        return new HBox(ApplicationController.SPACING, acceptButton, declineButton);
    }

    private void handleButtonAction(Player player, Label statusLabel, Button acceptButton, Button declineButton) {
        acceptButton.setOnAction(e -> {
            try {
                lobbyController.acceptGame(player);
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
                statusLabel.setText("Status: " + player.getPlayStatus());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        declineButton.setOnAction(e -> {
            try {
                lobbyController.declineGame(player);
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
                statusLabel.setText("Status: " + player.getPlayStatus());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void setButtonVisibility(Player player, Game game, Label statusLabel, Button acceptButton, Button declineButton) {
        if (player.getIdGame() == game.getId()) {
            if (player.getPlayStatus().equals(PlayStatus.CHALLENGER.toString())) {
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
                statusLabel.setText("Challenger");
            } else if (player.getPlayStatus().equals(PlayStatus.ACCEPTED.toString())) {
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
                statusLabel.setText("Accepted");
            } else if (player.getPlayStatus().equals(PlayStatus.REFUSED.toString())) {
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
                statusLabel.setText("Refused");
            } else if (player.getPlayStatus().equals(PlayStatus.CHALLENGEE.toString())) {
                acceptButton.setVisible(true);
                declineButton.setVisible(true);
                statusLabel.setText("Challengee");
            }
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(ApplicationController.SPACING);

        Label gamesOverviewLabel = new Label("Uitnodigingen");
        gamesOverviewLabel.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZEMEDIUM));
        gamesOverviewLabel.setTextFill(Color.WHITE);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshGames());
        HBox.setHgrow(gamesOverviewLabel, Priority.ALWAYS);

        header.getChildren().addAll(gamesOverviewLabel, refreshButton);
        return header;
    }
}
