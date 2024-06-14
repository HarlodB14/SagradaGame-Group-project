package View;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class PrivateObjectiveCardPane extends StackPane {

    private ImageView imageView;

    public PrivateObjectiveCardPane(String color) {
        imageView = new ImageView();
        imageView.setFitWidth(280);  // Adjust the width as needed
        imageView.setFitHeight(280);  // Adjust the height as needed
        imageView.setPreserveRatio(true);
        setCardImage(color);
        getChildren().add(imageView);
    }

    private void setCardImage(String color) {
        String imagePath = "Resources/images/ObjectiveCards/Private/" + color + ".png";
        Image image = new Image(imagePath);
        imageView.setImage(image);
    }
}
