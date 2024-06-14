package Controller;

import DAL.FavorTokenDAO;
import View.FavorTokenPane;

import java.sql.SQLException;

public class FavorTokenController {

    private final FavorTokenDAO favorTokenDAO;

    public FavorTokenController() {
        favorTokenDAO = new FavorTokenDAO();
    }

    public FavorTokenPane generateFavorTokenPane(int playerId) {
        try {
            int tokenCount = favorTokenDAO.getAllFromPlayer(playerId).size();
            return new FavorTokenPane(tokenCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
