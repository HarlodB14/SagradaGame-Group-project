package View;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class PublicObjectiveCardPane extends HBox{

    private ImageView[] imageViews;
    private int currentIndex;
    public PublicObjectiveCardPane(List<Integer> numberList) {
        // Create image paths corresponding to each number
        String[] imagePaths = {
                "Resources/images/ObjectiveCards/Public/1.png",
                "Resources/images/ObjectiveCards/Public/2.png",
                "Resources/images/ObjectiveCards/Public/3.png",
                "Resources/images/ObjectiveCards/Public/4.png",
                "Resources/images/ObjectiveCards/Public/5.png",
                "Resources/images/ObjectiveCards/Public/6.png",
                "Resources/images/ObjectiveCards/Public/7.png",
                "Resources/images/ObjectiveCards/Public/8.png",
                "Resources/images/ObjectiveCards/Public/9.png",
                "Resources/images/ObjectiveCards/Public/10.png",

        };

        imageViews = new ImageView[numberList.size()];


        // Set the initial visibility of the image views
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView();
            imageViews[i].setFitWidth(250);
            imageViews[i].setFitHeight(360);
            imageViews[i].setVisible(i == 0);
        }

        // Create a StackPane to overlay the images
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageViews);

        // Set alignment to center
        StackPane.setAlignment(stackPane, Pos.CENTER);

        // Add event handlers for sliding through the images
        stackPane.setOnMouseClicked(event -> {
            currentIndex = (currentIndex + 1) % imageViews.length;
            updateImageViews(numberList, imagePaths);
        });


        getChildren().add(stackPane);

        // Display initial images based on the number list
        updateImageViews(numberList, imagePaths);
    }
    private void updateImageViews(List<Integer> numberList, String[] imagePaths) {
        for (int i = 0; i < numberList.size(); i++) {
            int number = numberList.get(i);

            // Validate the number is within the range of image paths
            if (number >= 1 && number <= imagePaths.length) {
                // Get the corresponding image path
                String imagePath = imagePaths[number - 1];

                // Set the image for the current index
                imageViews[i].setImage(new Image(imagePath));

                // Set the visibility of the current index
                imageViews[i].setVisible(i == currentIndex);
            } else {
                //System.out.println("Invalid number: " + number);
            }
        }
    }
}
