package View;

import Controller.AccountController;
import Controller.ApplicationController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.SQLException;

public class RegisterPane extends GridPane {
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;

    public RegisterPane(AccountController accountController) {

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

        errorLabel = new Label("");
        add(errorLabel, 1, 4);

        Button registerButton = new Button("Registreren");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(registerButton);
        add(hbBtn, 1, 5);

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                if (username.length() < 3 || password.length() < 3) {
                    ApplicationController.popupMessage("Account Registratie Foutmelding", "Gebruikersnaam en/of wachtwoord is te kort", "Gebruikersnaam/wachtwoord 3 karakters of langer zijn", Alert.AlertType.WARNING);
                } else if (accountController.usernameExists(username)) {
                    ApplicationController.popupMessage("Account Registratie Foutmelding", "Gebruikersnaam is al in gebruik", "Gebruikersnaam is al in gebruik", Alert.AlertType.WARNING);
                } else {
                    ApplicationController.popupMessage("Account Registratie Succes", "Account aangemaakt", "Account is aangemaakt, je wordt nu ingelogd", Alert.AlertType.INFORMATION);
                    accountController.Register(username, password);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}

