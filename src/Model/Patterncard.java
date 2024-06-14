package Model;

import java.sql.SQLException;
import java.util.ArrayList;

public class Patterncard {
    private int patternCardID;
    private String name;
    private int playerID;
    private String playerUsername;
    private int difficulty;
    private int standard;
    private Square[] squares;
    private static final int PATTERN_CARD_SIZE = 20;  // Typically, the size might be fixed, but adjustable if needed
    public static final int ROWS = 4;
    public static final int COLUMNS = 5;
    public static final int MAX_AMOUNT = 24;


    public Patterncard(int patternCardID) {
        this.patternCardID = patternCardID;
    }
    
    public Patterncard(int patternCardID, int playerID, String playerUsername, String name, int difficulty, Square[] squaresFromFrameField) throws SQLException, SQLException {
        this.patternCardID = patternCardID;
        this.playerID = playerID;
        this.playerUsername = playerUsername;
        this.name = name;
        this.difficulty = difficulty;
        DAL.PatterncardDAO.loadPatterncardProperties(this);
        this.squares = new Square[PATTERN_CARD_SIZE];
        int index = 0;
        if (squaresFromFrameField != null) {
            for (Square square : squaresFromFrameField) {
                this.squares[index++] = square;
            }
        } else {
            for (int yPos = 1; yPos <= ROWS; yPos++) {
                for (int xPos = 1; xPos <= COLUMNS; xPos++) {
                    squares[index++] = new Square(patternCardID, xPos, yPos, playerID);
                }
            }
        }

    }

    //deze constructor wordt gebruikt om data op te halen via PatterncardDAO en daar nieuwe object voor te maken, vandaar dat name & difficulty als var hier staan- Harlod
    public Patterncard(int id, String name, int difficulty, int standard) {
        this.patternCardID = id;
        this.name = name;
        this.difficulty = difficulty;
        this.standard = standard;
    }

    public Square[] getSquares() {
        return squares;
    }

    public Square getSquare(int posX, int posY) {
        for (Square square : this.squares) {
            if (square.getXPos() == posX && square.getYPos() == posY) {
                return square;
            }
        }
        return null;
    }

    public void setSquares(ArrayList<Square> squares) {
        this.squares = squares.toArray(new Square[squares.size()]);
    }

    public int getPatternCardID() {
        return patternCardID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String toString() {
        return "PatternCard{" +
                "patternCardID=" + patternCardID +
                ", difficulty=" + difficulty +
                ", standard=" + standard +
                ", name='" + name + '\'' +
                '}';
    }
    
    public int getPlayerID() {
        return playerID;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }


    public void setDieOnSquare(Square square, Die selectedDie) {
        for (Square s : squares) {
            if (s.getXPos() == square.getXPos() && s.getYPos() == square.getYPos()) {
                s.setDie(selectedDie);
            }
        }
    }
}