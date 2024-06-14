package Model;

import DAL.DieDAO;
import Enumerations.Colour;

import java.sql.SQLException;

public class Die {
    public static final int MAX_DICE = 6;

    private int gameId;
    private int dieNumber;
    private String dieColor;
    private int eye;
    private int roundTrack;
    private int roundId; // Voeg deze toe

    public int getIdGame() {
        return gameId;
    }

    public Die(int gameId, int roundId, String dieColor, int eye, int dieNumber) {
        this.gameId = gameId;
        this.roundId = roundId;
        this.dieColor = dieColor;
        this.eye = eye;
        this.dieNumber = dieNumber;
    }

    public Die(String dieColor, int dieNumber, int gameId) {
        this.dieColor = dieColor;
        this.dieNumber = dieNumber;
        this.gameId = gameId;

        try {
            DieDAO dieDAO = new DieDAO();
            this.eye = dieDAO.getDieEyes(dieColor, dieNumber, gameId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getImagePath() {
        return "/images/" + this.eye + "_eyes.png";
    }

    public String getDieColor() {
        return dieColor;
    }

    public Colour getDieColorC() {
        return Colour.valueOf(dieColor.toUpperCase());
    }

    public void setEye(int eye) {
        this.eye = eye;
    }

    public int getRoundId() {
        return this.roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public int getDieNumber() {
        return dieNumber;
    }

    public int getRoundTrack() {
        return roundTrack;
    }

    public int getGameId() {
        return gameId;
    }

    public int getEyes() {
        return eye;
    }

    public void setRoundTrack(int roundTrack) {
        this.roundTrack = roundTrack;
    }
}
