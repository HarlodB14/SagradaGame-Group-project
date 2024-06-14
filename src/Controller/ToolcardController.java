package Controller;

import DAL.DieDAO;
import DAL.FavorTokenDAO;
import DAL.PlayerDAO;
import DAL.ToolCardDAO;
import Model.Die;
import Model.PurchaseHistoryEntry;
import Model.Toolcard;
import Model.ToolcardFactory;
import View.ToolcardPane;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolcardController {

    private final FavorTokenDAO favorTokenDAO;
    private final ToolCardDAO toolcardDAO;
    private final PlayerDAO playerDAO;
    private final GameController gameController;

    private final DieDAO dieDAO;

    public ToolcardController(GameController gameController) {
        this.favorTokenDAO = new FavorTokenDAO();
        this.toolcardDAO = new ToolCardDAO();
        this.gameController = gameController;
        this.playerDAO = new PlayerDAO();
        this.dieDAO = new DieDAO();
    }

    public void activate(int idgame, int toolcardId) throws SQLException {
        Toolcard toolcard = toolcardDAO.get(toolcardId);
        int cost = toolcard.getCost(gameController.getCurrentGame().getId());
        int playerId = new GameController(idgame).getCurrentPlayer().getIdPlayer();
        int currentRound = new GameController(idgame).getCurrentRound().getId();
        int favorTokens = favorTokenDAO.getAllFromPlayer(playerId).size();

        //toolcard wordt hier geactiveerd TODO: implemententie per card hier plaatsen,(Aparte methode gaat plaatsingregels adhv toolcard bepalen)
        toolcard.use(); //Dit houdt nu alleen bij dat de toolcard is gebruikt, implementatie per toolcard moet nog worden toegevoegd
        switch (toolcardId){
            case 1:
                //Driepuntstang
                useDriepuntstang();
                break;
            case 2:
                //Églomisé Borstel
                break;
            case 3:
                //Folie-aandrukker
                break;
            case 4:
                //Loodopenhaler
                break;
            case 5:
                //Rondsnijder
                break;
            case 6:
                //Fluxborstel
                break;
            case 7:
                //Loodhamer
                break;
            case 8:
                //Glasbreektang
                break;
            case 9:
                //Snijliniaal
                break;
            case 10:
                //Schuurblok
                useSchuurblok();
                break;
            case 11:
                //Fluxverwijderaar
                break;
            case 12:
                //Olieglassnijder
                break;
        }
        //favortoken wordt hier geplaatst op "Toolcard"
        //TODO: dit ook displayen in de view
    }

    private void useDriepuntstang() {
        try {
            // Step 1: Check if a die is selected
            Die selectedDie = gameController.getSelectedDie();
            if (selectedDie == null) {
                ApplicationController.popupMessage("Geen dobbelsteen geselecteerd", "Geen dobbelsteen geselecteerd", "Selecteer eerst een dobbelsteen", Alert.AlertType.ERROR);
                return;
            }

            // Step 2: Prompt user for +1 or -1
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Verander dobbelsteenwaarde");
            alert.setHeaderText("Wil je de waarde van de dobbelsteen verhogen of verlagen?");
            alert.setContentText("Kies een optie.");

            ButtonType buttonTypePlus = new ButtonType("+1");
            ButtonType buttonTypeMinus = new ButtonType("-1");
            ButtonType buttonTypeCancel = new ButtonType("Annuleer", ButtonType.CANCEL.getButtonData());

            alert.getButtonTypes().setAll(buttonTypePlus, buttonTypeMinus, buttonTypeCancel);

            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

            // Step 3: Change the die value based on the user's choice
            int newDieValue = selectedDie.getEyes();
            if (result == buttonTypePlus) {
                if (newDieValue == 6) {
                    ApplicationController.popupMessage("Ongeldige bewerking", "Ongeldige bewerking", "De waarde van de dobbelsteen kan niet worden verhoogd van 6 naar 1.", Alert.AlertType.ERROR);
                    return;
                }
                newDieValue++;
            } else if (result == buttonTypeMinus) {
                if (newDieValue == 1) {
                    ApplicationController.popupMessage("Ongeldige bewerking", "Ongeldige bewerking", "De waarde van de dobbelsteen kan niet worden verlaagd van 1 naar 6.", Alert.AlertType.ERROR);
                    return;
                }
                newDieValue--;
            } else {
                return; // If the user canceled, do nothing
            }

            // Step 4: Update the die value in the database
            selectedDie.setEye(newDieValue);
            dieDAO.updateEyes(selectedDie);

            // Step 5: Update the model and view
            gameController.refreshGameScene();
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationController.popupMessage("Error", "An error occurred while using the Driepuntstang.", "Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void useSchuurblok() {
        try {
            // Step 1: Check if a die is selected
            Die selectedDie = gameController.getSelectedDie();
            if (selectedDie == null) {
                ApplicationController.popupMessage("Geen dobbelsteen geselecteerd", "Geen dobbelsteen geselecteerd", "Selecteer eerst een dobbelsteen", Alert.AlertType.ERROR);
                return;
            }

            // Step 2: Calculate the inverted value
            int currentEyes = selectedDie.getEyes();
            int newDieValue;

            switch (currentEyes) {
                case 1:
                    newDieValue = 6;
                    break;
                case 2:
                    newDieValue = 5;
                    break;
                case 3:
                    newDieValue = 4;
                    break;
                case 4:
                    newDieValue = 3;
                    break;
                case 5:
                    newDieValue = 2;
                    break;
                case 6:
                    newDieValue = 1;
                    break;
                default:
                    ApplicationController.popupMessage("Ongeldige dobbelsteenwaarde", "Ongeldige dobbelsteenwaarde", "De waarde van de dobbelsteen is ongeldig.", Alert.AlertType.ERROR);
                    return;
            }

            // Step 3: Update the die value in the database
            selectedDie.setEye(newDieValue);
            dieDAO.updateEyes(selectedDie);

            // Step 4: Update the model and view
            gameController.refreshGameScene();
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationController.popupMessage("Error", "An error occurred while using Tool Card 10.", "Please try again.", Alert.AlertType.ERROR);
        }
    }

    public void buyToolcard(int idgame, int toolcardId, Stage detailStage) throws SQLException {
        Toolcard toolcard = (Toolcard) ToolcardFactory.createToolcard(toolcardId);
        int playerId = gameController.getCurrentPlayer().getIdPlayer();
        int roundId = gameController.getCurrentRound().getId();
        int favorTokens = favorTokenDAO.getAllFromPlayer(playerId).size();
        int gameToolCardId = toolcardDAO.getGameToolCardId(idgame, toolcardId);

        if (favorTokens >= toolcard.getCost(gameController.getCurrentGame().getId())) {
            toolcard.isBought = true;
            favorTokenDAO.placeOnGameToolCard(toolcard.getCost(gameController.getCurrentGame().getId()), idgame, playerId, gameToolCardId, roundId);
            gameController.refreshGameScene();
            detailStage.close();
            ApplicationController.popupMessage("Toolcard gekocht", "Je hebt de toolcard " + toolcard.getName() + " gekocht.", "Actie: " + toolcard.getDescription() + " Je kunt deze nu activeren", Alert.AlertType.INFORMATION);
        } else {
            ApplicationController.popupMessage("Te weinig betaalstenen", "Te weinig betaalstenen", "Je hebt te weinig betaalstenen om deze toolcard te kopen.", Alert.AlertType.ERROR);
        }
    }

    public ToolcardPane drawToolcards(int idgame) {
        try {
            List<Toolcard> gametoolcards = toolcardDAO.getGameToolcards(idgame);
            return new ToolcardPane(gametoolcards, this, gameController);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public VBox getPurchaseHistory(int idgame, int idToolCard) {
        VBox historyPane = new VBox();
        Label titleLabel = new Label("Geschiedenis");
        titleLabel.setFont(new Font(28));
        historyPane.getChildren().add(titleLabel);

        try {
            ArrayList<PurchaseHistoryEntry> history = favorTokenDAO.getPurchaseHistory(idgame, toolcardDAO.getGameToolCardId(idgame, idToolCard));

            for (PurchaseHistoryEntry entry : history) {
                String playerName = playerDAO.get(entry.getPlayerId()).getUsername();
                int price = entry.getTokens();

                HBox row = new HBox();
                Label nameLabel = new Label(playerName);
                nameLabel.setFont(new Font(16));

                row.getChildren().add(nameLabel);

                for (int i = 0; i < price; i++) {
                    Image favortokenImage = new Image("Resources/images/favortoken.png");
                    ImageView tokenImageView = new ImageView(favortokenImage);

                    tokenImageView.setFitWidth(favortokenImage.getWidth() / 5);
                    tokenImageView.setFitHeight(favortokenImage.getHeight() / 5);
                    row.getChildren().add(tokenImageView);
                }

                historyPane.getChildren().add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyPane;
    }

    public boolean hasBoughtTools() {
        try {
            return favorTokenDAO.hasBoughtToolCards(gameController.getCurrentGame().getId(), gameController.getCurrentPlayer().getIdPlayer(), gameController.getRoundId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
