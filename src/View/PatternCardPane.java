package View;

import Controller.GameController;
import Controller.PatternCardController;
import Controller.ScoreController;
import DAL.PlayerDAO;
import Model.Die;
import Model.Patterncard;
import Model.Square;
import Utilities.ImageStore;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;
import javafx.scene.text.FontPosture;

import java.sql.SQLException;

public class PatternCardPane extends GridPane {

    private final ImageStore imageStore = new ImageStore();

    private PatternCardController patterncardController;
    private ScoreController scoreController;

    private PlayerDAO playerDAO;

    private Patterncard patternCard;
    private int patternCardId;
    private GameController gameController;

    //Select PatternCard Pane
    public PatternCardPane(int patternCardId) {
        patterncardController = new PatternCardController();
        playerDAO = new PlayerDAO();
        scoreController = new ScoreController();
        this.patternCardId = patternCardId;
        patternCard = patterncardController.getPatternCard();

        setPrefSize(80, 80);
    }

    //GameScene Pane
    public PatternCardPane(int patternCardID, int playerID, String username, String name, int difficulty, Square[] squares, GameController gameController) throws SQLException {
        patterncardController = new PatternCardController(patternCardID, playerID, username, name, difficulty, squares);
        playerDAO = new PlayerDAO();
        patternCard = patterncardController.getPatternCard();
        scoreController = new ScoreController();
        this.gameController = gameController;
        createPatternCardGrid();
        setStyleBasedOnTurn();
    }


    private void createPatternCardGrid() throws SQLException {
        for (int yPos = 0; yPos < 4; yPos++) {
            for (int xPos = 0; xPos < 5; xPos++) {
                Square currentSquare = patternCard.getSquares()[yPos * 5 + xPos];
                StackPane squareStack = createSquareStack(currentSquare);
                add(squareStack, xPos, yPos);
            }
        }

        //name label
        Label usernameLabel = new Label(patternCard.getPlayerUsername());
        usernameLabel.setFont(Font.font("Arial", FontPosture.REGULAR, 12)); // Adjust the font size as needed
        add(usernameLabel, 0, Patterncard.ROWS, Patterncard.COLUMNS, 1);

        int score = playerDAO.getPlayerScore(patternCard.getPlayerID(), gameController.getCurrentGame().getId());
        if (playerDAO.getPlayerById(patternCard.getPlayerID()).getIdPlayer() != (gameController.getCurrentPlayer().getIdPlayer())){
            score -= scoreController.getPrivateScore(playerDAO.getPlayerById(patternCard.getPlayerID()), gameController.getGame());
        }
        Label scoreLabel = new Label("score:" + score );
        scoreLabel.setFont(Font.font("Arial", FontPosture.REGULAR, 12)); // Adjust the font size as needed
        add(scoreLabel, 4, Patterncard.ROWS, Patterncard.COLUMNS, 1);
    }

    private StackPane createSquareStack(Square currentSquare) {
        double width = 65.0;
        double height = 65.0;
        Rectangle squareView = new Rectangle(width, height, Color.WHITE); // Default to white
        squareView.setStroke(Color.BLACK);
        squareView.setStrokeWidth(2);

        Label colorLabel = new Label();
        colorLabel.setFont(Font.font("Arial", FontPosture.REGULAR, 18)); // Smaller font size to fit in corner
        colorLabel.setTextFill(Color.WHITE);

        if (currentSquare != null) {
            if (currentSquare.getDie() != null) {
                Die die = currentSquare.getDie();
                squareView.setFill(Color.valueOf(die.getDieColor()));
                ImageView dieImageView = new ImageView(imageStore.getDieImage(die));
                dieImageView.setFitWidth(width);
                dieImageView.setFitHeight(height);
                colorLabel.setText(die.getDieColor().substring(0,1).toUpperCase());
                StackPane.setAlignment(colorLabel, Pos.BOTTOM_RIGHT);
                StackPane stackPane = new StackPane(squareView, dieImageView, colorLabel);
                stackPane.setOnMouseClicked(e -> gameController.placeDie(currentSquare));
                return stackPane;
            } else {
                if (currentSquare.getColor() != null) {
                    squareView.setFill(Color.valueOf(currentSquare.getColor().toString()));
                    colorLabel.setText(currentSquare.getColorString().toString().substring(0,1).toUpperCase());
                    StackPane.setAlignment(colorLabel, Pos.BOTTOM_RIGHT);
                }
                if (currentSquare.getEyes() > 0) {
                    squareView.setFill(Color.GRAY);
                    Label valueLabel = new Label(Integer.toString(currentSquare.getEyes()));
                    valueLabel.setFont(Font.font("Arial", 30));
                    valueLabel.setTextFill(Color.WHITE);
                    StackPane stackPane = new StackPane(squareView, valueLabel, colorLabel);
                    stackPane.setOnMouseClicked(e -> gameController.placeDie(currentSquare));
                    return stackPane;
                }
            }
        }
        StackPane stackPane = new StackPane(squareView, colorLabel);
        stackPane.setOnMouseClicked(e -> gameController.placeDie(currentSquare));
        return stackPane;
    }

    private void setStyleBasedOnTurn() {
        if (gameController.getTurnPlayerId() == patternCard.getPlayerID()) {
            this.setStyle("-fx-border-color: green; -fx-border-width: 5px;");
        } else {
            this.setStyle("-fx-border-color: transparent;");
        }
    }

    public void updatePatternCardGrid() throws SQLException {
        this.getChildren().clear();
        this.patternCard = this.patterncardController.getPatternCard();
        createPatternCardGrid();
    }

    public int getPatternCardId() {
        return patternCardId;
    }
}
