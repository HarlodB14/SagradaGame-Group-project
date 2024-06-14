package Model;

import javafx.beans.property.*;

public class PlayerStats {
    private StringProperty username = new SimpleStringProperty();
    private IntegerProperty gamesPlayed = new SimpleIntegerProperty();
    private IntegerProperty wins = new SimpleIntegerProperty();
    private StringProperty wlRatio = new SimpleStringProperty();
    private IntegerProperty highScore = new SimpleIntegerProperty();
    private StringProperty mostPlacedColor = new SimpleStringProperty();
    private IntegerProperty mostPlacedValue = new SimpleIntegerProperty();
    private IntegerProperty numOpponents = new SimpleIntegerProperty();

    public PlayerStats(String username, int gamesPlayed, int wins, int highScore, String mostPlacedColor, int mostPlacedValue, int numOpponents) {
        setUsername(username);
        setGamesPlayed(gamesPlayed);
        setWins(wins);
        updateWLRatio();
        setHighScore(highScore);
        setMostPlacedColor(mostPlacedColor);
        setMostPlacedValue(mostPlacedValue);
        setNumOpponents(numOpponents);
    }

    private void updateWLRatio() {
        int losses = gamesPlayed.get() - wins.get();
        setWLRatio(wins.get() + " - " + losses);
    }

    // Username
    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    // Games Played
    public int getGamesPlayed() {
        return gamesPlayed.get();
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed.set(gamesPlayed);
        updateWLRatio();
    }

    public IntegerProperty gamesPlayedProperty() {
        return gamesPlayed;
    }

    // Wins
    public int getWins() {
        return wins.get();
    }

    public void setWins(int wins) {
        this.wins.set(wins);
        updateWLRatio();
    }

    public IntegerProperty winsProperty() {
        return wins;
    }

    // Win-Loss Ratio
    public String getWLRatio() {
        return wlRatio.get();
    }

    public void setWLRatio(String wlRatio) {
        this.wlRatio.set(wlRatio);
    }

    public StringProperty wlRatioProperty() {
        return wlRatio;
    }

    // High Score
    public int getHighScore() {
        return highScore.get();
    }

    public void setHighScore(int highScore) {
        this.highScore.set(highScore);
    }

    public IntegerProperty highScoreProperty() {
        return highScore;
    }

    // Most Placed Color
    public String getMostPlacedColor() {
        return mostPlacedColor.get();
    }

    public void setMostPlacedColor(String mostPlacedColor) {
        this.mostPlacedColor.set(mostPlacedColor);
    }

    public StringProperty mostPlacedColorProperty() {
        return mostPlacedColor;
    }

    // Most Placed Value
    public int getMostPlacedValue() {
        return mostPlacedValue.get();
    }

    public void setMostPlacedValue(int mostPlacedValue) {
        this.mostPlacedValue.set(mostPlacedValue);
    }

    public IntegerProperty mostPlacedValueProperty() {
        return mostPlacedValue;
    }

    // Number of Opponents
    public int getNumOpponents() {
        return numOpponents.get();
    }

    public void setNumOpponents(int numOpponents) {
        this.numOpponents.set(numOpponents);
    }

    public IntegerProperty numOpponentsProperty() {
        return numOpponents;
    }
}
