package View;

import Controller.ApplicationController;
import Controller.CreategameController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateGameScene extends Scene {

    private final CreategameController creategameController;

    private BorderPane layout;
    private VBox invitedPlayersBox;
    private VBox playerListBox;
    private Button createButton;
    private ArrayList<String> invitedPlayerNames;

    private static final Insets PANE_PADDING = new Insets(10);
    private static final Insets LABEL_PADDING = new Insets(5);
    private static final int INVITE_BUTTON_HEIGHT = 40;
    private static final int INVITE_BUTTON_WIDTH = 200;
    private static final int PANE_SPACING = 10;

    public CreateGameScene(CreategameController creategameController) {
        super(new StackPane(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        this.creategameController = creategameController;

        setLayout();

        this.setRoot(layout);
    }

    private void setLayout() {
        invitedPlayerNames = new ArrayList<>();

        layout = new BorderPane();
        layout.setBackground(ApplicationController.BACKGROUNDCOLOR_DARKGREY);

        Button backButton = new Button("Terug");
        backButton.setOnAction(e -> {
            ApplicationController.stage.setScene(new DashboardScene());
        });

        HBox topBar = new HBox(backButton);
        topBar.setPadding(PANE_PADDING);
        topBar.setBackground(ApplicationController.BACKGROUNDCOLOR_DARKGREY);

        layout.setTop(topBar);

        createButton = new Button("Aanmaken");
        createButton.setOnAction(e -> {
            try {
                if (!creategameController.canInvitePlayers(invitedPlayerNames)) {
                    return;
                }
                creategameController.createGame(invitedPlayerNames);
                ApplicationController.switchScene(new DashboardScene());
                ApplicationController.popupMessage("Gelukt!", "Spelers succesvol uitgenodigd", "Het spel is nu zichtbaar in het overzicht.", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        createButton.setDisable(true);

        HBox bottomBar = new HBox();
        bottomBar.setPadding(PANE_PADDING);
        bottomBar.setBackground(ApplicationController.BACKGROUNDCOLOR_DARKGREY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomBar.getChildren().addAll(spacer, createButton);

        layout.setBottom(bottomBar);

        playerListBox = new VBox();
        playerListBox.setSpacing(PANE_SPACING);
        playerListBox.getChildren().add(createLabel("Alle Accounts"));

        invitedPlayersBox = new VBox();
        invitedPlayersBox.setSpacing(PANE_SPACING);
        invitedPlayersBox.getChildren().add(createLabel("Uitgenodigde Accounts"));

        try {
            List<String> allAccountNames = creategameController.getAllAccountNames();

            for (String accountName : allAccountNames) {
                playerListBox.getChildren().add(createInviteButton(accountName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ScrollPane playerListScroll = new ScrollPane(playerListBox);
        playerListScroll.setPadding(PANE_PADDING);
        playerListScroll.setMaxHeight(Double.MAX_VALUE);
        playerListScroll.prefWidthProperty().bind(layout.widthProperty().divide(2));

        ScrollPane invitedPlayersScroll = new ScrollPane(invitedPlayersBox);
        invitedPlayersScroll.setPadding(PANE_PADDING);
        invitedPlayersScroll.setMaxHeight(Double.MAX_VALUE);
        invitedPlayersScroll.prefWidthProperty().bind(layout.widthProperty().divide(2));

        layout.setCenter(new HBox(playerListScroll, invitedPlayersScroll));
    }

    private Button createInviteButton(String username) {
        Button inviteButton = new Button(username);
        inviteButton.setPrefSize(INVITE_BUTTON_WIDTH, INVITE_BUTTON_HEIGHT);
        inviteButton.setStyle("-fx-font-size: 14px;");

        inviteButton.setOnAction(e -> {
            if (playerListBox.getChildren().contains(inviteButton)) {
                playerListBox.getChildren().remove(inviteButton);
                invitedPlayersBox.getChildren().add(inviteButton);
                invitedPlayerNames.add(username);
            } else {
                invitedPlayersBox.getChildren().remove(inviteButton);
                playerListBox.getChildren().add(inviteButton);
                invitedPlayerNames.remove(username);
            }
            updatecreateInviteButtonState();
        });

        return inviteButton;
    }

    private Label createLabel(String text) {
        Label label = new Label();
        label.setText(text);
        label.setPadding(LABEL_PADDING);
        label.setStyle("-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #333333; " +
                "-fx-padding: 5px;");
        return label;
    }

    private void updatecreateInviteButtonState() {
        int invitedPlayersCount = invitedPlayersBox.getChildren().size() - 1;
        createButton.setDisable(invitedPlayersCount == 0 || invitedPlayersCount > 3);
    }

}
