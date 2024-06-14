package View;


import Controller.AccountController;
import Controller.ApplicationController;
import Controller.CreategameController;
import Controller.LobbyController;

import Controller.*;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class DashboardScene extends Scene {

    private VBox layout;
    private VBox buttonsPane;

    private Button createGameButton;
    private Button gameOverviewButton;
    private Button logoutButton;
    private Button exitButton;

    public DashboardScene() {
        super(new StackPane(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        createButtons();
        setLayout();
        this.setRoot(layout);
    }

    public void setLayout() {
        layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(ApplicationController.BACKGROUNDCOLOR_DARKGREY);
        layout.setPrefSize(ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        layout.setSpacing(20);

        buttonsPane = new VBox();
        buttonsPane.setSpacing(10);
        buttonsPane.setAlignment(Pos.CENTER);
        createButtons();
        buttonsPane.getChildren().addAll(createGameButton, gameOverviewButton, logoutButton, exitButton);

        //logo inladen
        String image_path = "/Resources/images/logo.png";
        Image logo = new Image(image_path);
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(ApplicationController.SAGRADA_LOGO_WIDTH);
        logoView.setFitHeight(ApplicationController.SAGRADA_LOGO_HEIGHT);
        layout.getChildren().add(logoView);

        layout.getChildren().add(buttonsPane);
    }

    public void createButtons() {
        createGameButton = new Button("Spel aanmaken");
        createGameButton.setPrefSize(ApplicationController.BUTTONWIDTH, ApplicationController.BUTTONHEIGHT);
        createGameButton.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        createGameButton.setOnAction(event -> {
            ApplicationController.stage.setScene(new CreateGameScene(new CreategameController()));
        });

        gameOverviewButton = new Button("Spel overzicht");
        gameOverviewButton.setPrefSize(ApplicationController.BUTTONWIDTH, ApplicationController.BUTTONHEIGHT);
        gameOverviewButton.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        gameOverviewButton.setOnAction(event -> {
            ApplicationController.stage.setScene(new LobbyScene(new LobbyController()));
        });

        logoutButton = new Button("Uitloggen");
        logoutButton.setPrefSize(ApplicationController.BUTTONWIDTH, ApplicationController.BUTTONHEIGHT);
        logoutButton.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
        logoutButton.setOnAction(event -> {
            AccountController accountController = new AccountController();
            accountController.logout();
            // After logout, switch back to the login scene
            accountController.switchToStartupScene();
        });

        exitButton = new Button("Afsluiten");
        exitButton.setOnAction(event -> {
            ApplicationController.closeApplication();
        });
        exitButton.setPrefSize(ApplicationController.BUTTONWIDTH, ApplicationController.BUTTONHEIGHT);
        exitButton.setFont(ApplicationController.generateFont(ApplicationController.FONTARIAL, ApplicationController.FONTSIZESMALL));
    }
}