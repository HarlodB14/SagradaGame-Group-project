package View;

import Controller.ApplicationController;
import Controller.GameController;
import Controller.LobbyController;
import Model.Player;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EndGameScene extends Scene {
    private TableView<Player> table = new TableView<>();
    private GameController gameController;

    public EndGameScene(GameController gameController) {
        super(new VBox(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        this.gameController = gameController;

        Button backButton = new Button("Terug");
        backButton.setOnAction(e -> ApplicationController.switchScene(new LobbyScene(new LobbyController())));

        // Load players and determine the winner
        List<Player> sortedPlayers = loadPlayers();
        String winnerName = sortedPlayers.isEmpty() ? "N/A" : sortedPlayers.get(0).getUsername();
        Label winnerLabel = new Label("Het spel is voorbij, de winnaar is " + winnerName + "!");
        winnerLabel.setStyle("-fx-font-size: 40px;");

        HBox topBar = new HBox(backButton, winnerLabel);
        topBar.setAlignment(Pos.CENTER); // Center the contents of the HBox
        topBar.setSpacing(10); // Space between the button and the label
        topBar.setPrefWidth(ApplicationController.WINDOW_WIDTH); // Set the preferred width to the width of the application

        setupTableView();

        VBox root = (VBox) this.getRoot();
        root.getChildren().addAll(topBar, table);
    }

    private void setupTableView() {
        TableColumn<Player, Integer> rankCol = new TableColumn<>("#");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("ranking"));

        TableColumn<Player, String> nameCol = new TableColumn<>("Speler");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Player, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        table.getColumns().addAll(rankCol, nameCol, scoreCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Make columns fit the table width
    }

    private List<Player> loadPlayers() {
        List<Player> sortedPlayers = gameController.getPlayers().stream()
                .sorted(Comparator.comparing(Player::getScore).reversed())
                .collect(Collectors.toList());

        // Assign ranks
        IntStream.range(0, sortedPlayers.size())
                .forEach(idx -> sortedPlayers.get(idx).setRanking(idx + 1));

        ObservableList<Player> observablePlayers = FXCollections.observableArrayList(sortedPlayers);
        table.setItems(observablePlayers);
        return sortedPlayers;
    }
}
