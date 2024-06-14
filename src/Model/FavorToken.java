package Model;

public class FavorToken {
    private int idFavorToken;
    private int idGame;
    private Integer roundID;
    private Integer idPlayer;
    private Integer gameToolCard;

    public FavorToken(int idFavorToken, int idGame, Integer roundID, Integer idPlayer, Integer gameToolCard) {
        this.idFavorToken = idFavorToken;
        this.idGame = idGame;
        this.roundID = roundID;
        this.idPlayer = idPlayer;
        this.gameToolCard = gameToolCard;
    }

    public FavorToken() {
    }

    public int getIdFavorToken() {
        return idFavorToken;
    }

    public void setIdFavorToken(int idFavorToken) {
        this.idFavorToken = idFavorToken;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public Integer getRoundID() {
        return roundID;
    }

    public void setRoundID(Integer roundID) {
        this.roundID = roundID;
    }

    public Integer getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(Integer idPlayer) {
        this.idPlayer = idPlayer;
    }

    public Integer getGameToolCard() {
        return gameToolCard;
    }

    public void setGameToolCard(Integer gameToolCard) {
        this.gameToolCard = gameToolCard;
    }

    public String toString() {
        return "FavorToken{" +
                "idFavorToken=" + idFavorToken +
                ", idGame=" + idGame +
                ", roundID=" + roundID +
                ", idPlayer=" + idPlayer +
                ", gameToolCard=" + gameToolCard +
                '}';
    }

}
