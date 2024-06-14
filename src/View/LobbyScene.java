package View;

import Controller.ApplicationController;
import Controller.LobbyController;
import Controller.ChatController;
import DAL.ChatDAO;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LobbyScene extends Scene {
    private CurrentGamesPane currentGamesPane;
    private GameOverviewPane overviewPane;
    private InvitePane invitePane;
    private PlayerStatsPane playerStatsPane;
    private LobbyController lobbyController;
    private MenuBar menuBar;
    private double newHeight;

    public LobbyScene(LobbyController lobbyController) {
        super(new VBox(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        this.lobbyController = lobbyController;

        currentGamesPane = new CurrentGamesPane(newHeight, lobbyController);
        invitePane = new InvitePane(newHeight, lobbyController);
        overviewPane = new GameOverviewPane(lobbyController);
        playerStatsPane = new PlayerStatsPane(lobbyController);

        setupMenu();
        adjustScrollPaneHeight();
        switchToCurrentGamesPane();
    }

    private void setupMenu() {
        menuBar = new MenuBar();

        Menu menuHome = new Menu("Home");
        MenuItem homeItem = new MenuItem("Home");
        homeItem.setOnAction(event -> switchToHomePane());
        menuHome.getItems().add(homeItem);

        Menu menuGameOverview = new Menu("Spel Overzicht");
        MenuItem busyItem = new MenuItem("Huidige Spellen");
        MenuItem overviewItem = new MenuItem("Alle Spellen");
        MenuItem statsItem = new MenuItem("Speler Resultaten");
        busyItem.setOnAction(event -> switchToCurrentGamesPane());
        overviewItem.setOnAction(event -> switchToOverviewPane());
        statsItem.setOnAction(event -> switchToPlayerStatsPane());
        menuGameOverview.getItems().addAll(busyItem, overviewItem, statsItem);

        Menu menuInvitations = new Menu("Spel Uitnodigingen");
        MenuItem invitationsItem = new MenuItem("Bekijk Uitnodigingen");
        invitationsItem.setOnAction(event -> switchToInvitePane());
        menuInvitations.getItems().add(invitationsItem);

        // Add Chat menu item
        Menu menuChat = new Menu("Chat");
        MenuItem chatItem = new MenuItem("Chat");
        menuChat.getItems().add(chatItem);

        menuBar.getMenus().addAll(menuHome, menuGameOverview, menuInvitations, menuChat);
        VBox root = (VBox) getRoot();
        root.getChildren().add(menuBar);
    }

    private void switchToHomePane() {
        VBox root = (VBox) getRoot();
        root.getChildren().set(1, new Pane());
        VBox.setVgrow(new Pane(), Priority.ALWAYS);
        ApplicationController.switchScene(new DashboardScene());
    }

    private void switchToInvitePane() {
        VBox root = (VBox) getRoot();
        root.getChildren().set(1, invitePane);
        VBox.setVgrow(invitePane, Priority.ALWAYS);
    }

    private void switchToCurrentGamesPane() {
        VBox root = (VBox) getRoot();
        if (root.getChildren().size() > 1) {
            root.getChildren().set(1, currentGamesPane);
        } else {
            root.getChildren().add(currentGamesPane);
        }
    }

    private void switchToOverviewPane() {
        VBox root = (VBox) getRoot();
        root.getChildren().set(1, overviewPane);
        VBox.setVgrow(overviewPane, Priority.ALWAYS);
    }


    private void switchToPlayerStatsPane() {
        VBox root = (VBox) getRoot();
        root.getChildren().set(1, playerStatsPane);
        VBox.setVgrow(playerStatsPane, Priority.ALWAYS);
    }

    private void adjustScrollPaneHeight() {
        Platform.runLater(() -> {
            double menuBarHeight = menuBar.getHeight();
            double newHeight = this.getHeight() - menuBarHeight;

            currentGamesPane.adjustHeight(newHeight);
            overviewPane.adjustHeight(newHeight);
            invitePane.adjustHeight(newHeight);
            playerStatsPane.adjustHeight(newHeight);
        });
    }
}
