package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FavorTokenPane extends VBox {

    private final double PANE_HEIGHT = 200;
    private final double PANE_WIDTH = 100;
    private final double TOKEN_SPACING = 5;
    private final Insets PANE_MARGIN = new Insets(0, 0, 15, 0);
    private final Insets PANE_PADDING = new Insets(10);
    private final double IMAGE_SCALE_FACTOR = 4;

    public FavorTokenPane(int tokenCount) {
        setPadding(PANE_PADDING);
        setHeight(PANE_HEIGHT);
        setWidth(PANE_WIDTH);

        Label label = new Label("Betaalstenen");
        label.setFont(new Font(20));
        VBox.setMargin(label, PANE_MARGIN);

        VBox tokensContainer = new VBox();
        tokensContainer.setSpacing(TOKEN_SPACING);

        Image favortokenImage = new Image("Resources/images/favortoken.png");

        HBox currentRow = new HBox();
        currentRow.setSpacing(TOKEN_SPACING);

        for (int i = 0; i < tokenCount; i++) {
            ImageView tokenImageView = new ImageView(favortokenImage);
            tokenImageView.setFitWidth(favortokenImage.getWidth() / IMAGE_SCALE_FACTOR);
            tokenImageView.setFitHeight(favortokenImage.getHeight() / IMAGE_SCALE_FACTOR);
            currentRow.getChildren().add(tokenImageView);

            if ((i + 1) % 8 == 0 || i == tokenCount - 1) {
                tokensContainer.getChildren().add(currentRow);
                currentRow = new HBox();
                currentRow.setSpacing(TOKEN_SPACING);
            }
        }

        getChildren().addAll(label, tokensContainer);

        if (tokenCount == 0) {
            Label empty = new Label("-");
            getChildren().add(empty);
        }
    }

    public void setTokenCount(int tokenCount) {

    }
}
