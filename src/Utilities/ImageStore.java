package Utilities;

import Controller.GameController;
import Enumerations.Colour;
import Model.Die;
import Model.Patterncard;
import Model.Toolcard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class ImageStore {
    private static final int GENERAL_DICE_RATIO = 60;
    private static final String DICE_IMAGE_FOLDER = "/Resources/images/dice/";
    private static final String PATTERNCARD_IMAGE_FOLDER = "/Resources/images/PatternCards/";
    private static final String DICE_PREFIX = "";
    private static final String FILE_EXT = ".png";
    private static final BlendMode DICE_BLENDMODE = BlendMode.DARKEN;
    private static final String TOOLCARD_IMAGE_FOLDER = "/Resources/images/ToolCards/";

    private final ArrayList<Image> diceImages = new ArrayList<>();
    private final ArrayList<Image> patterncardImages = new ArrayList<>();
    private final ArrayList<Image> toolcardImages = new ArrayList<>();

    public ImageStore() {
        for (int i = 1; i <= Die.MAX_DICE; i++) {
            String path = DICE_IMAGE_FOLDER + i + "_eyes" + FILE_EXT;
            Image dice = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            diceImages.add(dice);
        }

        for (int i = 1; i <= Patterncard.MAX_AMOUNT; i++) {
            String patterncardPath = PATTERNCARD_IMAGE_FOLDER + "patterncard" + i + FILE_EXT;
            Image patterncard = new Image(Objects.requireNonNull(getClass().getResourceAsStream(patterncardPath)));
            patterncardImages.add(patterncard);
        }
    }

    public Pane GetDice(Die die, GameController gameController) {
        int index = die.getEyes() - 1; // Ensure this uses getEyes() if it refers to the number of dots on the die.

        ImageView diceImg = new ImageView(diceImages.get(index));
        diceImg.setFitWidth(GENERAL_DICE_RATIO);
        diceImg.setFitHeight(GENERAL_DICE_RATIO); // Ensuring the image fits the pane
        diceImg.setPreserveRatio(true);

        // Create a stack pane to allow for overlay of text over the image
        StackPane dicePane = new StackPane();
        dicePane.setPrefSize(GENERAL_DICE_RATIO, GENERAL_DICE_RATIO);

        // Creating a background based on the die color
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf(die.getDieColorC().toString()), new CornerRadii(5), Insets.EMPTY);
        dicePane.setBackground(new Background(backgroundFill));

        if (gameController.getSelectedDie() != null && gameController.getSelectedDie().equals(die)) {
            dicePane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))));
        }

        // Create and configure the color label
        Label colorLabel = new Label(die.getDieColor().substring(0, 1).toUpperCase()); // Get first letter, uppercase
        colorLabel.setFont(Font.font("Arial", FontPosture.REGULAR, 18)); // Set font size
        colorLabel.setTextFill(Color.WHITE); // Set text color
        StackPane.setAlignment(colorLabel, Pos.BOTTOM_RIGHT); // Align to bottom right

        // Add both the image and the label to the stack pane
        dicePane.getChildren().addAll(diceImg, colorLabel);

        dicePane.setOnMouseClicked(e -> gameController.setSelectedDie(die));

        return dicePane;
    }


    public Image getDieImage(Die die) {
        String path = DICE_IMAGE_FOLDER + die.getEyes() + "_eyes.png";
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public Image getToolcardImage(Toolcard toolcard) {
        String formattedName = toolcard.getName().toLowerCase();
        String path = TOOLCARD_IMAGE_FOLDER + toolcard.getIdToolcard() + "-" + formattedName + FILE_EXT;
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }


    public Pane getPatterncardPane(int patterncardId) {
        if (patterncardId >= 1 && patterncardId <= patterncardImages.size()) {
            ImageView patterncardImg = new ImageView(patterncardImages.get(patterncardId - 1));
            return new Pane(patterncardImg);
        } else {
            return new Pane();
        }
    }

    public Pane GetPatternCardDice(Die die, double width, double height) {
        int index = die.getEyes() - 1;

        Pane dicePane = new Pane();
        ImageView diceImg = new ImageView(diceImages.get(index));
        diceImg.setFitWidth(width);
        diceImg.setFitHeight(height);
        diceImg.setPreserveRatio(true);
        diceImg.setCache(true);
        diceImg.setBlendMode(DICE_BLENDMODE);
        dicePane.getChildren().add(diceImg);
        dicePane.setBackground(getDiceBackground(die.getDieColorC()));

        return dicePane;
    }


    private Background getDiceBackground(Colour colour) {
        return new Background(new BackgroundFill(Paint.valueOf(colour.toString()), new CornerRadii(5d), Insets.EMPTY));
    }
}
