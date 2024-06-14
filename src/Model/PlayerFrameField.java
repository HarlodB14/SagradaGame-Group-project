package Model;

public class PlayerFrameField {
    private int playerID;
    private int positionX;
    private int positionY;
    private int gameID;
    private int dieNumber;
    private String dieColor;

    // Constructor
    public PlayerFrameField(int gameID, int playerID, int positionX, int positionY, int dieNumber, String dieColor) {
        this.gameID = gameID;
        this.playerID = playerID;
        this.positionX = positionX;
        this.positionY = positionY;
        this.dieNumber = dieNumber;
        this.dieColor = dieColor;
    }

    // Getters and Setters
    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getDieNumber() {
        return dieNumber;
    }

    public void setDieNumber(int dieNumber) {
        this.dieNumber = dieNumber;
    }

    public String getDieColor() {
        return dieColor;
    }

    public void setDieColor(String dieColor) {
        this.dieColor = dieColor;
    }
}
