package Model;

public interface IToolcard {
    int getIdToolcard();

    String getName();

    String getDescription();

    int getCost(int idgame);

    void use();
}
