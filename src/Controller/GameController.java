package Controller;

import DAL.*;
import Model.Patterncard;
import Model.Player;
import Model.Die;
import Model.Game;
import Model.*;
import View.DicePane;
import View.EndGameScene;
import View.GameScene;
import javafx.scene.control.Alert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameController {

    private final PatterncardDAO patterncardDAO;
    private final PlayerDAO playerDAO;
    private final GameDAO gameDAO;
    private RoundTrackController roundTrackController;


    private final ScoreController scoreController;

    private final int FINAL_ROUND_ID = 20;


    private Game game;
    private HashMap<Integer, Integer> seqnrByPlayers;
    private boolean gameHasEnded = false;
    private boolean hasPlacedDie;

    public GameController(int gameId) throws SQLException {
        this.game = new Game(gameId);
        playerDAO = new PlayerDAO();
        scoreController = new ScoreController();
        patterncardDAO = new PatterncardDAO();
        gameDAO = new GameDAO();
        roundTrackController = new RoundTrackController(this);
    }

    public GameController(int gameid, int playerID) throws SQLException {
        this.game = new Game(gameid, playerID);
        playerDAO = new PlayerDAO();
        scoreController = new ScoreController();
        patterncardDAO = new PatterncardDAO();
        gameDAO = new GameDAO();
        roundTrackController = new RoundTrackController(this);
        seqnrByPlayers = playerDAO.getSeqrByPlayers(gameid);
        hasPlacedDie = false;
    }

    public List<Integer> getPublicObjectiveCardNumbers() {
        List<Integer> list = new ArrayList<>();
        List<Public_ObjectiveCard> publicObjectiveCards = game.getPublicObjectiveCards();

        for (Public_ObjectiveCard card : publicObjectiveCards) {
            list.add(card.getIdPublicObjectiveCard());
        }

        return list;
    }

    public void nextRound() {
        // Verplaats overgebleven dobbelstenen naar roundtrack
        roundTrackController.nextRound();

        if (getRoundId() >= FINAL_ROUND_ID) {
            gameHasEnded = true;
            setPlayerPlaystatusToFinished();
            ApplicationController.switchScene(new EndGameScene(this));
        } else {
            updateGameModelInDatabase();

            for (Player player : getPlayers()) {
                int newSeqNr = (player.getSeqNr() == 1) ? getNumberOfPlayers() : player.getSeqNr() - 1;
                player.setSeqNr(newSeqNr);
                player.updateModelInDatabase();
            }

            refreshGameScene();
        }
    }

    private void setPlayerPlaystatusToFinished() {
        for (Player player : getPlayers()) {
            player.setPlayStatus(String.valueOf(PlayStatus.FINISHED));
        }
        playerDAO.setAllPlayersToFinished(game.getId());
    }

    public void nextTurn() {
        try {
            gameHasEnded = false;
            Player finishedPlayer = playerDAO.get(game.getTurnIdPlayer());
            int currentSeqNr = finishedPlayer.getSeqNr();
            int totalPlayers = getNumberOfPlayers();
            Round currentRound = new Round(game.getCurrentRoundId());
            scoreController.updateScore(playerDAO.getAll(), game);
            hasPlacedDie = false;

            int newSeqNr = 0;

            if (currentRound.isClockwise()) {
                if (currentSeqNr == totalPlayers) {
                    setRoundId(getRoundId() + 1);
                    newSeqNr = currentSeqNr;
                } else {
                    newSeqNr = currentSeqNr + 1;
                }
            } else {
                if (currentSeqNr == 1) {
                    setRoundId(getRoundId() + 1);
                    nextRound();
                    newSeqNr = 1;
                } else {
                    newSeqNr = currentSeqNr - 1;
                }
            }
            if (!gameHasEnded) {
                seqnrByPlayers = playerDAO.getSeqrByPlayers(game.getId());
                game.setTurnIdPlayer(seqnrByPlayers.get(newSeqNr));
                game.setHasPlacedDie(false);
                updateGameModelInDatabase();
                refreshGameScene();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedPatternCard(int patternCardId) {
        Player currentPlayer = getCurrentPlayer();
        FavorTokenDAO favorTokenDAO = new FavorTokenDAO();
        try {
            patterncardDAO.updateidPatterncard(patternCardId, currentPlayer.getIdPlayer());
            favorTokenDAO.assignFavorTokensFromPatternCard(patterncardDAO.get(patternCardId), currentPlayer.getIdPlayer(), currentPlayer.getIdGame());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Player getCurrentPlayer() {
        try {
            return playerDAO.getCurrentPlayerByName(ApplicationController.currentLoggedInAccount.getUsername(), game.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Game getCurrentGame() {
        return game;
    }

    public void setSelectedDie(Die selectedDie) {
        if (getTurnPlayerId() == getCurrentPlayer().getIdPlayer()) {
            this.game.setSelectedDie(selectedDie);
            ApplicationController.switchScene(new GameScene(this, new PatternCardController(), true));
            //System.out.println("Selected die: " + selectedDie.getDieColor() + " " + selectedDie.getEyes());
        }

    }

    public void placeDie(Square square) {
        //System.out.println("Clicked on square at position (" + square.getXPos() + ", " + square.getYPos() + ")");
        if (hasPlacedDie()) {
            ApplicationController.popupMessage("Al een dobbelsteen geplaatst", "Al een dobbelsteen geplaatst", "Je kunt maar één dobbelsteen per beurt plaatsen!", Alert.AlertType.ERROR);
            return;
        } else if (game.getTurnIdPlayer() != game.getPlayerID()) {
            ApplicationController.popupMessage("Niet jouw beurt", "Niet jouw beurt", "Je kan alleen dobbelstenen plaatsen tijdens jouw beurt", Alert.AlertType.ERROR);
            return;
        } else if (game.getSelectedDie() == null) {
            ApplicationController.popupMessage("Geen dobbelsteen geselecteerd", "Geen dobbelsteen geselecteerd", "Selecteer eerst een dobbelsteen", Alert.AlertType.ERROR);
            return;
        } else if (square.getPlayerID() != game.getPlayerID()) {
            ApplicationController.popupMessage("Niet jouw bord", "Niet jouw bord", "Je kan alleen dobbelstenen plaatsen op jouw eigen bord", Alert.AlertType.ERROR);
            return;
        } else if (square.getDie() != null) {
            ApplicationController.popupMessage("Veld bezet", "Veld bezet", "Dit veld is al bezet", Alert.AlertType.ERROR);
            return;
        } else if (square.getColor() != null && square.getColor() != game.getSelectedDie().getDieColorC()) {
            ApplicationController.popupMessage("Verkeerde kleur", "Verkeerde kleur", "De kleur van de dobbelsteen komt niet overeen met de kleur van het veld", Alert.AlertType.ERROR);
            return;
        } else if (square.getEyes() != 0 && square.getEyes() != game.getSelectedDie().getEyes()) {
            ApplicationController.popupMessage("Verkeerd aantal ogen", "Verkeerd aantal ogen", "Het aantal ogen van de dobbelsteen komt niet overeen met het aantal ogen van het veld", Alert.AlertType.ERROR);
            return;
        }

        PlacementResult placementResult = game.checkDiePlacement(square);
        if (!placementResult.isValid()) {
            ApplicationController.popupMessage("Plaatsingsfout", "Plaatsingsfout", placementResult.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        game.placeDieOnPlayerFrameField(square);
        setHasPlacedDie(true);
        // Redraw the GameScene
        updateGameAfterDiePlaced(square);
    }

    private void updateGameAfterDiePlaced(Square square) {
        this.game.updateGameAfterDiePlaced(square);
        refreshGameScene();
    }

    public void refreshGameScene() {
        this.game = new Game(game.getId(), getCurrentPlayer().getIdPlayer());

        ApplicationController.switchScene(new GameScene(this, new PatternCardController(), true));
    }

    public List<Patterncard> getPatterncards() {
        return game.getPatterncards();
    }

    public List<Die> getAvailableDice() {
        return game.getAvailableDie();
    }

    public Round getCurrentRound() {
        return game.getCurrentRound();
    }

    public int getRoundId() {
        return game.getCurrentRoundId();
    }

    public void setRoundId(int roundId) {
        game.setCurrentRoundId(roundId);
    }

    public void updateGameModelInDatabase() {
        try {
            gameDAO.update(game);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNameFromPlayerTurn() {
        return game.getTurnIdPlayerName();
    }

    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    private int getNumberOfPlayers() {
        return game.getNumberOfPlayers();
    }

    public int getRoundNr() {
        if (game.getCurrentRoundId() > 20) {
            return 11;
        }

        Round round = new Round(game.getCurrentRoundId());
        return round.getRoundNr();
    }

    public int getTurnPlayerId() {
        return game.getTurnIdPlayer();
    }

    public void refreshGame() {
        if (game.getSelectedDie() == null) {
            game = new Game(game.getId(), getCurrentPlayer().getIdPlayer());
        } else {
            Die selectedDie = game.getSelectedDie();
            game = new Game(game.getId(), getCurrentPlayer().getIdPlayer(), selectedDie);
        }
    }

    public Die getSelectedDie() {
        return game.getSelectedDie();
    }

    public Game getGame() {
        return game;
    }

    public void showEndGameScene() {
        ApplicationController.switchScene(new EndGameScene(this));
    }

    public void setHasPlacedDie(boolean hasPlacedDie) {
        this.hasPlacedDie = hasPlacedDie;
    }

    public boolean hasPlacedDie() {
        return this.hasPlacedDie;
    }
}
