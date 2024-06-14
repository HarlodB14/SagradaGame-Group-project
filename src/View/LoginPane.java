package View;

import Controller.AccountController;
import Controller.ApplicationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.SQLException;

public class LoginPane extends GridPane {
    private TextField usernameField;
    private PasswordField passwordField;

    public LoginPane(AccountController accountController) {

        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Gebruikersnaam:");
        add(usernameLabel, 0, 1);

        usernameField = new TextField();
        add(usernameField, 1, 1);

        Label passwordLabel = new Label("Wachtwoord:");
        add(passwordLabel, 0, 2);

        passwordField = new PasswordField();
        add(passwordField, 1, 2);

        Button loginButton = new Button("Inloggen");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        add(hbBtn, 1, 4);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                if (accountController.exists(username, password)) {
                    accountController.Login(username, password);
                } else {
                    ApplicationController.popupMessage("Account Inlog Foutmelding", "Account niet gevonden", "Gebruikersnaam of wachtwoord is incorrect", Alert.AlertType.ERROR);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

}
