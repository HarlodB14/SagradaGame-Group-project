package Controller;

import DAL.DatabaseConnector;
import Model.Account;
import View.GameScene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ApplicationController {

    /* Constants voor properties binnen scenes of stage */
    public static final int WINDOW_WIDTH = 1440;
    public static final int WINDOW_HEIGHT = 810;
    //    voor fullscreen miss wel handig:
//    public static Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//    public static double WINDOW_WIDTH = screenBounds.getWidth();
//    public static double WINDOW_HEIGHT = screenBounds.getHeight();
    /* Deze gebruiken voor grootte tekstomschrijvingen*/
    public static final int FONTSIZEMEDIUM = 32;
    /* Deze gebruiken voor grootte tekst in buttons*/
    public static final int FONTSIZESMALL = 16;
    public static final String FONTARIAL = "Arial";
    public static final String FONTLUCIDAHANDWRITING = "Lucida Handwriting";
    public static final int BUTTONWIDTH = 200;
    public static final int BUTTONHEIGHT = 50;
    public static final Background BACKGROUNDCOLOR_DARKGREY = new Background(new BackgroundFill(Color.rgb(49, 51, 56), CornerRadii.EMPTY, Insets.EMPTY));
    public static final int SAGRADA_LOGO_WIDTH = 400;
    public static final int SAGRADA_LOGO_HEIGHT = 200;
    public static final Background CARD_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY));
    public static final Color LABEL_TEXT_COLOR = Color.WHITE;
    public static final Insets COMMON_PADDING = new Insets(10);
    public static final int SPACING = 10;

    public static Stage stage;

    public static Account currentLoggedInAccount;

    private static Scene currentScene;

    public ApplicationController() {
        ApplicationController.stage = new Stage();
    }

    /* hier zet je methods m.b.t besturing/acties/styling van de stages/scene/applicatie zelf */

    public static void popupMessage(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.initOwner(ApplicationController.stage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Font generateFont(String family, int fontSize) {
        return Font.font(family, FontWeight.MEDIUM, fontSize);
    }

    public static void closeApplication() {
        //DB verbinding ook afsluiten als je programma afsluit
        DatabaseConnector.closeConnection();
        Platform.exit();
    }

    public static void switchScene(Scene scene) {
        if (currentScene instanceof GameScene) {
            ((GameScene) currentScene).stopRefreshThread();
            Pane root = (Pane) currentScene.getRoot();
            if (root instanceof BorderPane) {
                ((BorderPane) root).getChildren().clear();
            } else if (root instanceof StackPane) {
                ((StackPane) root).getChildren().clear();
            }
        }

        currentScene = scene;
        stage.setScene(scene);

        if (currentLoggedInAccount != null) {
            stage.setTitle("Sagrada - ingelogd als: " + currentLoggedInAccount.getUsername());
        } else {
            stage.setTitle("Sagrada");
        }
    }

    public void setStageProperties() {
        stage.setTitle("Sagrada");
//        stage.setWidth(WINDOW_WIDTH);
//        stage.setHeight(WINDOW_HEIGHT);
//        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.getIcons().add(new Image("Resources/images/icon.png"));
    }

}
