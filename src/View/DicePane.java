package View;

import Controller.GameController;
import Model.Die;
import Utilities.ImageStore;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.util.List;

public class DicePane extends HBox {
    private static final int DIE_SIZE = 60;  // Size of each die image
    private GameController gameController;
    private ImageStore imageStore;

    public DicePane(List<Die> availableDice, GameController gameController) {
        super(10);  // spacing between dice
        this.imageStore = new ImageStore();  // Utilize ImageStore for fetching dice images
        this.gameController = gameController;
        initializeDiceDisplay(availableDice);
    }

    private void initializeDiceDisplay(List<Die> dice) {
        for (Die die : dice) {
            Pane dieView = imageStore.GetDice(die, gameController);
            this.getChildren().add(dieView);
        }
    }
}