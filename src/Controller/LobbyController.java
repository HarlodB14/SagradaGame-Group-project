package Controller;

import DAL.GameDAO;
import DAL.PlayerDAO;
import DAL.PlayerStatsDAO;
import Model.Game;
import Model.PlayStatus;
import Model.Player;
import Model.PlayerStats;
import View.EndGameScene;
import View.GameScene;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LobbyController {

    private final GameDAO gameDAO;
    private final PlayerDAO playerDAO;

    public LobbyController() {
        gameDAO = new GameDAO();
        playerDAO = new PlayerDAO();
    }

    public List<Game> getAllGames() throws SQLException {
        List<Game> games = null;
        try {
            games = gameDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public void acceptGame(Player player) throws SQLException {
        player.setPlayStatus(PlayStatus.ACCEPTED.toString());
        PlayStatus status = PlayStatus.valueOf(player.getPlayStatus());
        playerDAO.updatePlayStatus(player.getIdPlayer(), status);
    }

    public void declineGame(Player player) throws SQLException {
        player.setPlayStatus(PlayStatus.REFUSED.toString());
        PlayStatus status = PlayStatus.valueOf(player.getPlayStatus());
        playerDAO.updatePlayStatus(player.getIdPlayer(), status);
    }

    public Player getPlayer(int gameId) throws SQLException {
        String username = ApplicationController.currentLoggedInAccount.getUsername();
        return playerDAO.getCurrentPlayerByName(username, gameId);
    }

    public void checkJoinPossibility(Player player) throws SQLException {
        Game currentGame = getCurrentGameByPlayer(player);
        List<Player> players = playerDAO.getPlayersByGameId(currentGame.getId());

        boolean allPlayersAccepted = true;
        boolean gameIsFinished = false;
        boolean isChallenger = player.getPlayStatus().equals(PlayStatus.CHALLENGER.toString());

        for (Player currentPlayer : players) {
            if (currentPlayer != null) {
                if (!Objects.equals(currentPlayer.getPlayStatus(), PlayStatus.CHALLENGER.toString())) {
                    switch (currentPlayer.getPlayStatus()) {
                        case "FINISHED":
                            ApplicationController.switchScene(new EndGameScene(new GameController(currentGame.getId(), player.getIdPlayer())));
                            return;
                        case "CHALLENGER":
                            if (isChallenger) {
                                if (otherPlayersAccepted(players) && gameIsFinished == false) {
                                    allPlayersAccepted = true;
                                }
                            }
                            break;
                        case "ACCEPTED":
                            break;
                        default:
                            allPlayersAccepted = false;
                            break;
                    }
                }
            }
        }
        if (allPlayersAccepted) {
            goToGameScene(player);
        } else {
            ApplicationController.popupMessage("Error", "Je kan nog niet deelnemen", "Uitgenodigde spelers hebben nog niet geaccepteerd of iemand heeft geweigerd", Alert.AlertType.ERROR);
        }
    }

    private void goToGameScene(Player player) throws SQLException {
        if (hasSelectedPatterncard(player)) {
            GameScene gameScene = new GameScene(new GameController(getCurrentGameByPlayer(player).getId(), player.getIdPlayer()), new PatternCardController(), true);
            gameScene.removePatternCardOptions();
            ApplicationController.switchScene(gameScene);
        } else {
            GameController gameController = new GameController(getCurrentGameByPlayer(player).getId());
            ApplicationController.switchScene(new GameScene(gameController, new PatternCardController()));
            gameController.getGame().setCurrentPlayerID(player.getIdPlayer());

        }
    }

    private boolean hasSelectedPatterncard(Player player) {
        boolean isSelected = true;
        try {
            isSelected = getCurrentPatterncard(player) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSelected;
    }

    private int getCurrentPatterncard(Player player) throws SQLException {
        return playerDAO.getIdPatternCard(player.getIdPlayer(), player.getIdGame());
    }

    private Game getCurrentGameByPlayer(Player currentPlayer) throws SQLException {
        return gameDAO.get(currentPlayer.getIdGame());
    }

    private boolean otherPlayersAccepted(List<Player> players) {
        boolean otherPlayers = true;
        for (Player player : players) {
            if (player != null) {
                if (Objects.equals(player.getPlayStatus(), PlayStatus.REFUSED.toString())) {
                    otherPlayers = false;
                }
            }
        }
        return otherPlayers;
    }

    public void setSelectedGameId(int idGame, Player player) {
        player.setIdGame(idGame);
    }

    public List<Player> getAllPlayers() throws SQLException {
        return playerDAO.getAll();
    }

    public List<Player> getAllCurrentPlayers(String username) throws SQLException {
        if (username != null) {
            return playerDAO.getAllCurrentPlayers(username);
        } else {
            return Collections.emptyList();
        }
    }

    public List<PlayerStats> getPlayerStats() throws SQLException {
        PlayerStatsDAO statsDAO = new PlayerStatsDAO();
        return statsDAO.getPlayerStats();
    }


    public List<Game> getGamesSortedByDate(boolean ascending) throws SQLException {
        return gameDAO.getGamesSortedByDate(ascending);
    }

}
