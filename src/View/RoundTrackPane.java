package View;

import Controller.RoundTrackController;
import Model.Die;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class RoundTrackPane extends VBox {
    private RoundTrackController roundTrackController;
    private HBox roundTrack;
    private VBox layout;
    private int gameId;

    public RoundTrackPane(int gameId, RoundTrackController roundTrackController) {
        this.gameId = gameId;
        this.roundTrackController = roundTrackController; // Pass the gameId to the controller
        initializeUI();
    }

    private void initializeUI() {
        // Horizontal box for dice slots
        roundTrack = new HBox(5); // Space between slots
        roundTrack.setAlignment(Pos.CENTER); // Center the HBox in the VBox
        roundTrack.setPadding(new Insets(10)); // Padding within the HBox for space around slots

        // Create 10 numbered slots for dice
        for (int i = 1; i <= 10; i++) {
            VBox diceSlotBox = new VBox(2); // VBox to place number above the slot
            diceSlotBox.setAlignment(Pos.TOP_CENTER); // Center the number and slot in the VBox

            Text label = new Text(String.valueOf(i));
            label.setFill(Color.BLACK); // Black text color for numbers

            StackPane diceSlot = new StackPane(); // StackPane to place dice in the slot
            diceSlot.setAlignment(Pos.TOP_LEFT); // Align dice at the top-left corner
            diceSlot.setPadding(new Insets(2)); // Add padding within each dice slot
            Rectangle rect = new Rectangle(60, 140); // Size of each slot (taller to fit 5.5 dice)
            rect.setFill(Color.WHITE); // Fill color of slots
            rect.setStroke(Color.BLACK); // Black border for visibility

            diceSlot.getChildren().add(rect);
            diceSlotBox.getChildren().addAll(label, diceSlot);
            roundTrack.getChildren().add(diceSlotBox);
        }

        // Organize round track in a VBox
        layout = new VBox(10);
        layout.getChildren().addAll(roundTrack);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(10)); // Extra padding for a nice appearance

        // Add the VBox to the pane
        this.getChildren().add(layout);

        // Refresh round track for the initial state
        refreshRoundTrack();
    }

    public void refreshRoundTrack() {
        for (int i = 0; i < roundTrack.getChildren().size(); i++) {
            VBox diceSlotBox = (VBox) roundTrack.getChildren().get(i);
            StackPane diceSlot = (StackPane) diceSlotBox.getChildren().get(1);
            diceSlot.getChildren().removeIf(node -> node instanceof HBox); // Remove existing dice representations

            // Restore the border color if there are no dice
            diceSlot.getChildren().stream()
                    .filter(node -> node instanceof Rectangle)
                    .forEach(node -> ((Rectangle) node).setStroke(Color.BLACK));
        }

        for (int i = 1; i <= roundTrackController.getCurrentRound(); i++) {
            List<Die> dice = roundTrackController.getDiceForCurrentRound(i);
            VBox diceSlotBox = (VBox) roundTrack.getChildren().get(i - 1);
            StackPane diceSlot = (StackPane) diceSlotBox.getChildren().get(1);

            int numDice = dice.size();
            int cols = 2; // Number of columns

            double dieSize = 20; // Smaller die size

            diceSlot.setMinHeight(140);
            diceSlot.setMinWidth(60);

            for (int j = 0; j < numDice; j++) {
                Die die = dice.get(j);

                // Create an HBox to hold the die and the color text
                HBox dieContainer = new HBox(2);
                dieContainer.setAlignment(Pos.TOP_LEFT);
                dieContainer.setPadding(new Insets(2, 2, 0, 2)); // Add padding to the left for space between the border and the color letter

                // Create a StackPane to represent the die
                StackPane dieStack = new StackPane();
                dieStack.setPrefSize(dieSize, dieSize);
                dieStack.setMaxSize(dieSize, dieSize);
                dieStack.setStyle("-fx-background-color: " + getColorHex(die.getDieColor()) + "; -fx-border-color: black;");
                dieStack.setAlignment(Pos.CENTER);

                // Add the eyes as white circles in the correct positions
                addDieEyes(dieStack, die.getEyes(), dieSize);

                // Add the color abbreviation as a Text node to the left
                Text colorText = new Text(getColorAbbreviation(die.getDieColor()));
                colorText.setFont(new Font(10)); // Make the color text smaller
                colorText.setFill(Color.BLACK); // Set text color to black for better readability

                dieContainer.getChildren().addAll(colorText, dieStack);

                // Position the die container within the dice slot
                dieContainer.setTranslateX((j % cols) * (dieSize + 10)); // Add space between dice horizontally
                dieContainer.setTranslateY((j / cols) * (dieSize + 10)); // Start positioning at the top

                diceSlot.getChildren().add(dieContainer);
            }

            // Keep the border always visible
            diceSlot.getChildren().stream()
                    .filter(node -> node instanceof Rectangle)
                    .forEach(node -> ((Rectangle) node).setStroke(Color.BLACK));
        }
    }

    private void addDieEyes(StackPane dieStack, int eyes, double dieSize) {
        double[][][] positions = {
                {{}},
                {{0.5, 0.5}}, // 1 eye
                {{0.25, 0.25}, {0.75, 0.75}}, // 2 eyes
                {{0.25, 0.25}, {0.5, 0.5}, {0.75, 0.75}}, // 3 eyes
                {{0.25, 0.25}, {0.75, 0.75}, {0.25, 0.75}, {0.75, 0.25}}, // 4 eyes
                {{0.25, 0.25}, {0.75, 0.75}, {0.25, 0.75}, {0.75, 0.25}, {0.5, 0.5}}, // 5 eyes
                {{0.25, 0.25}, {0.75, 0.75}, {0.25, 0.75}, {0.75, 0.25}, {0.25, 0.5}, {0.75, 0.5}} // 6 eyes
        };

        // Create and position the circles (eyes)
        for (int i = 0; i < eyes; i++) {
            Circle eye = new Circle(dieSize / 10, Color.WHITE);
            eye.setStroke(Color.BLACK);
            eye.setStrokeWidth(1);

            eye.setTranslateX((positions[eyes][i][0] - 0.5) * dieSize);
            eye.setTranslateY((positions[eyes][i][1] - 0.5) * dieSize);
            dieStack.getChildren().add(eye);
        }
    }

    // Helper method to convert color to hex string
    private String getColorHex(String color) {
        switch (color) {
            case "red":
                return "#FF0000";
            case "green":
                return "#00FF00";
            case "blue":
                return "#0000FF";
            case "yellow":
                return "#FFFF00";
            case "purple":
                return "#800080";
            default:
                return "#FFFFFF";
        }
    }

    // Helper method to get color abbreviation
    private String getColorAbbreviation(String color) {
        switch (color) {
            case "red":
                return "R";
            case "green":
                return "G";
            case "blue":
                return "B";
            case "yellow":
                return "Y";
            case "purple":
                return "P";
            default:
                return "";
        }
    }
}
