package Model;

import DAL.PlayerDAO;

import java.sql.SQLException;

public class Player {
    private int idPlayer = 0;
    private String username;
    private int idGame;
    private PlayStatus playStatus;
    private String playStatusString;
    private Integer seqNr;
    private String privateObjectiveCardColor;
    private Integer idPatternCard;
    private Integer score;
    private int ranking;

    public Player() {
    }


    //wordt gebruikt om getCurrentPlayerByName te kunnen gebruiken in PlayerDAO
    public Player(int playerId, String username, int idGame, PlayStatus playStatus, String privateObjectiveCardColor, int idPatternCard, int seqNr, int score) {
        this.idPlayer = playerId;
        this.username = username;
        this.playStatus = playStatus;
        this.privateObjectiveCardColor = privateObjectiveCardColor;
        this.idPatternCard = idPatternCard;
        this.seqNr = seqNr;
        this.score = score;
        this.idGame = idGame;
    }

    //deze wordt gebruikt om kopie te maken van player zodat je niet de originele player aanpast
    public Player(Player playerToCopy) {
        this.idPlayer = playerToCopy.idPlayer;
        this.username = playerToCopy.username;
        this.idGame = playerToCopy.idGame;
        this.playStatus = playerToCopy.playStatus;
        this.seqNr = playerToCopy.seqNr;
        this.privateObjectiveCardColor = playerToCopy.privateObjectiveCardColor;
        this.idPatternCard = playerToCopy.idPatternCard;
        this.score = playerToCopy.score;
    }

    public Player(int idPlayer, String username, int idGame, String playStatus, Integer seqNr, String privateObjectiveCardColor, Integer idPatternCard, Integer score) {
        this.idPlayer = idPlayer;
        this.username = username;
        this.idGame = idGame;
        this.playStatusString = playStatus;
        this.seqNr = seqNr;
        this.privateObjectiveCardColor = privateObjectiveCardColor;
        this.idPatternCard = idPatternCard;
        this.score = score;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public String getPlayStatus() {
        return playStatus.toString();
    }

    public void setPlayStatus(String playStatus) {
        this.playStatus = PlayStatus.valueOf(playStatus);
    }

    public Integer getSeqNr() {
        return seqNr;
    }

    public void setSeqNr(Integer seqNr) {
        this.seqNr = seqNr;
    }

    public String getPrivateObjectiveCardColor() {
        return privateObjectiveCardColor;
    }

    public void setPrivateObjectiveCardColor(String privateObjectiveCardColor) {
        this.privateObjectiveCardColor = privateObjectiveCardColor;
    }

    public Integer getIdPatternCard() {
        return idPatternCard;
    }

    public void setIdPatternCard(Integer idPatternCard) {
        this.idPatternCard = idPatternCard;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void updateModelInDatabase() {
        PlayerDAO playerDAO = new PlayerDAO();

        try {
            playerDAO.update(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getRanking() {
        return ranking;
    }
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }


}
