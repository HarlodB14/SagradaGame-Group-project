package View;

import Controller.LobbyController;
import Model.PlayerStats;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class PlayerStatsPane extends VBox {
    private TableView<PlayerStats> table = new TableView<>();

    public PlayerStatsPane(LobbyController controller) {
        TableColumn<PlayerStats, String> nameCol = new TableColumn<>("Naam");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<PlayerStats, String> wlCol = new TableColumn<>("W-L");
        wlCol.setCellValueFactory(cellData -> cellData.getValue().wlRatioProperty());

        TableColumn<PlayerStats, Number> highScoreCol = new TableColumn<>("High Score");
        highScoreCol.setCellValueFactory(cellData -> cellData.getValue().highScoreProperty());

        TableColumn<PlayerStats, String> mostPlacedColorCol = new TableColumn<>("Meest geplaatste kleur");
        mostPlacedColorCol.setCellValueFactory(cellData -> cellData.getValue().mostPlacedColorProperty());

        TableColumn<PlayerStats, Number> mostPlacedValueCol = new TableColumn<>("Meest geplaatste waarde");
        mostPlacedValueCol.setCellValueFactory(cellData -> cellData.getValue().mostPlacedValueProperty());

        TableColumn<PlayerStats, Number> numOpponentsCol = new TableColumn<>("Aantal tegenstanders");
        numOpponentsCol.setCellValueFactory(cellData -> cellData.getValue().numOpponentsProperty());

        table.getColumns().addAll(nameCol, wlCol, highScoreCol, mostPlacedColorCol, mostPlacedValueCol, numOpponentsCol);

        try {
            table.getItems().setAll(controller.getPlayerStats());
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        this.getChildren().add(table);
    }

    public void adjustHeight(double newHeight) {
        if (table != null) {
            table.setPrefHeight(newHeight);
        }
    }
}
