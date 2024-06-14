package Controller;

import DAL.FavorTokenDAO;
import DAL.PlayerDAO;
import DAL.PlayerFrameFieldDAO;
import Model.Game;
import Model.Player;

import java.sql.SQLException;
import java.util.List;

public class ScoreController {

    private final ObjectiveCardController objectiveCardController;
    private final FavorTokenDAO FTDAO;
    private final PlayerFrameFieldDAO PFFDAO;
    private final PlayerDAO PDAO;
    public ScoreController() {
        objectiveCardController = new ObjectiveCardController();
        FTDAO = new FavorTokenDAO();
        PFFDAO = new PlayerFrameFieldDAO();
        PDAO = new PlayerDAO();
    }


    public void updateScore(List<Player> players, Game game) throws SQLException {

        for (Player player : players) {
            int score = -20;
            score += FTDAO.getAllFromPlayer(player.getIdPlayer()).stream().count();
            score += objectiveCardController.checkObjectives(player, PFFDAO.getAllForPlayer(game.getId(), player.getIdPlayer()), game);

            player.setScore(score);
            PDAO.update(player);

        }
    }

    public int getPrivateScore(Player player, Game game) throws SQLException {
        return objectiveCardController.checkPrivateObjectives(player, PFFDAO.getAllForPlayer(game.getId(), player.getIdPlayer()));
    }
}
