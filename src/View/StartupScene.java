package View;

import Controller.AccountController;
import Controller.ApplicationController;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;

public class StartupScene extends Scene {

    public StartupScene(AccountController accountController) {
        super(new TabPane(), ApplicationController.WINDOW_WIDTH, ApplicationController.WINDOW_HEIGHT);
        TabPane tabPane = (TabPane) getRoot();

        LoginPane loginPane = new LoginPane(accountController);
        Tab loginTab = new Tab("Inloggen", loginPane);

        RegisterPane registerPane = new RegisterPane(accountController);
        Tab registerTab = new Tab("Registreren", registerPane);

        //PrivateObjectiveCardPane cardPane = new PrivateObjectiveCardPane("red");
        //Tab cardTab = new Tab("card", cardPane);

//        List<Integer> numberList = new ArrayList<>();
//        numberList.add(1);
//        numberList.add(4);
//        numberList.add(6);
//
//        PublicObjectiveCardPane cardspane = new PublicObjectiveCardPane(numberList);
//        Tab cardsTab = new Tab("cards", cardspane);

        tabPane.getTabs().addAll(loginTab, registerTab);  //cardTab cardsTab

    }

}
