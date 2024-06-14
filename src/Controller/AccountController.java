package Controller;

import DAL.AccountDAO;
import Model.Account;
import View.DashboardScene;
import View.StartupScene;
import javafx.scene.control.Alert;

import java.sql.SQLException;


public class AccountController {

    AccountDAO accountDAO;
    StartupScene startupScene;

    public AccountController() {
        this.startupScene = new StartupScene(this);
        this.accountDAO = new AccountDAO();
    }

    public void Register(String username, String password) throws SQLException {
        Account account = new Account(username, password);
        if (accountDAO.usernameExists(username)) {
            ApplicationController.popupMessage("Gebruikersnaam in gebruik", "Gebruikersnaam in gebruik", "Je gekozen gebruikersnaam is al in gebruik, kies een andere naam.", Alert.AlertType.WARNING);
        } else {
            accountDAO.insert(account);

            ApplicationController.currentLoggedInAccount = account;
            ApplicationController.switchScene(new StartupScene(this));
        }

        ApplicationController.switchScene(new DashboardScene());

    }

    public void Login(String username, String password) throws SQLException {
        Account account = new Account(username, password);

        if (accountDAO.exists(account)) {
            ApplicationController.currentLoggedInAccount = account;
        }

        ApplicationController.switchScene(new DashboardScene());
    }

    public void logout() {
        ApplicationController.currentLoggedInAccount = null;
        switchToStartupScene();
    }

    public void switchToStartupScene() {
        ApplicationController.switchScene(startupScene);
    }


    public Boolean exists(String username, String password) throws SQLException {
        return accountDAO.exists(new Account(username, password));
    }

    public Boolean usernameExists(String username) throws SQLException {
        return accountDAO.usernameExists(username);
    }
}

