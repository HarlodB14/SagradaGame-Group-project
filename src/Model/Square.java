package Model;

import DAL.SquareDAO;
import Enumerations.Colour;

import java.sql.SQLException;

public class Square {
    private int patternCardID;
    private int playerID;
    private int xPos;
    private int yPos;
    private Die die;
    private Colour color;
    private String colorString;
    private int eyes;
    private SquareDAO sqaureDAO = new SquareDAO();

    public Square(int patternCardID, int xPos, int yPos, int playerID) throws SQLException {
        this.patternCardID = patternCardID;
        this.xPos = xPos;
        this.yPos = yPos;
        this.playerID = playerID;
        sqaureDAO.loadProperties(this);
    }

//    public Color getDieColour() {
//        if (die != null && die.getColour() != null) {
//            return Color.valueOf(die.getColour().toString().toUpperCase());
//        }
//        return Color.WHITE;
//    }

    public int getPatternCardID() {
        return patternCardID;
    }
    public int getPlayerID() {
        return playerID;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public Die getDie() {
        return die;
    }

    public void setDie(Die die) {
        this.die = die;
    }

    public Colour getColor() {
        return color;
    }

    public void setColor(Colour color) {
        this.color = color;
    }

    public int getEyes() {
        return eyes;
    }

    public void setEyes(int eyes) {
        this.eyes = eyes;
    }

    public void setColorString(String color) {
        this.colorString = color;
    }

    public String getColorString() {
        return colorString;
    }
}
