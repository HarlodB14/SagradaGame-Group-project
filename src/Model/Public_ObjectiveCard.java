package Model;

// PublicObjectiveCard.java
public class Public_ObjectiveCard {
    private int idPublicObjectiveCard;
    private String name;
    private String description;
    private int points;

    public Public_ObjectiveCard(int idPublicObjectiveCard, String name, String description, int points) {
        this.idPublicObjectiveCard = idPublicObjectiveCard;
        this.name = name;
        this.description = description;
        this.points = points;
    }

    // Getters and setters
    public int getIdPublicObjectiveCard() {
        return idPublicObjectiveCard;
    }

    public void setIdPublicObjectiveCard(int idPublicObjectiveCard) {
        this.idPublicObjectiveCard = idPublicObjectiveCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
