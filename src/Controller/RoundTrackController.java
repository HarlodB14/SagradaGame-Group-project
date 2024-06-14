package Controller;

import DAL.RoundTrackDAO;
import Model.Die;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoundTrackController {
    private final RoundTrackDAO roundTrackDAO;
    private final GameController gameController;
    private final int gameId;
    private final int FINAL_ROUND_NR = 10;

    private int currentRound;
    private int currentRoundId;

    public RoundTrackController(GameController gameController) {
        this.roundTrackDAO = new RoundTrackDAO();
        this.currentRound = gameController.getRoundNr();
        this.currentRoundId = gameController.getRoundId();
        this.gameId = gameController.getGame().getId();
        this.gameController = gameController;
    }

    public List<Die> getDiceForCurrentRound(int roundNr) {
        try {
            return roundTrackDAO.getDiceForRound(gameId, roundNr);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void nextRound() {
        if (currentRound <= FINAL_ROUND_NR) { // Maximum 10 rounds
            try {
                // CurrentRoundId is updated before this method so -1
                // Dice are saved in the database by their first roundId in the round so -1 again
                currentRoundId = gameController.getRoundId() - 2;
                currentRound = gameController.getRoundNr() - 1;

                ArrayList<Die> leftoverDice = roundTrackDAO.getLeftoverDice(gameId, currentRoundId);
                System.out.println("Leftover dice for round " + currentRound + ": " + leftoverDice);

                roundTrackDAO.updateRoundTrackForDice(gameId, currentRound, leftoverDice);

                leftoverDice.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentRound() {
        return currentRound;
    }
}
