package Model;

import DAL.RoundDAO;

import java.sql.SQLException;

public class Round {

    private int id;
    private int roundNr;
    private boolean isClockwise;

    public Round(int roundId) {
        RoundDAO roundDao = new RoundDAO();

        try {
            Round roundData = roundDao.get(roundId);
            this.id = roundId;
            this.roundNr = roundData.getRoundNr();
            this.isClockwise = roundData.isClockwise();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Round(int id, int roundNr, boolean isClockwise) {
        this.id = id;
        this.roundNr = roundNr;
        this.isClockwise = isClockwise;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoundNr(int roundNr) {
        this.roundNr = roundNr;
    }

    public int getRoundNr() {
        return roundNr;
    }

    public boolean isClockwise() {
        return isClockwise;
    }

    public void setIsClockwise(boolean isClockwise) {
        this.isClockwise = isClockwise;
    }


}