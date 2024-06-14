package Model;

// GameObjectiveCard_Public.java
public class GameObjectiveCard_Public {
    private int idGame;
    private int idPublicObjectiveCard;

    public GameObjectiveCard_Public(int idGame, int idPublicObjectiveCard) {
        this.idGame = idGame;
        this.idPublicObjectiveCard = idPublicObjectiveCard;
    }

    // Getters and setters
    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getIdPublicObjectiveCard() {
        return idPublicObjectiveCard;
    }

    public void setIdPublicObjectiveCard(int idPublicObjectiveCard) {
        this.idPublicObjectiveCard = idPublicObjectiveCard;
    }
}
