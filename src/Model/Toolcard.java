package Model;

import DAL.FavorTokenDAO;
import DAL.ToolCardDAO;

import java.sql.SQLException;

public class Toolcard implements IToolcard {

    private final int id;
    private final FavorTokenDAO favorTokenDAO;
    private final ToolCardDAO toolCardDAO;

    private final int idgame;
    private final String name;
    private final String description;
    public boolean isBought;
    private int timesUsed;

    //data van gameToolcard table
    public Toolcard(int id, String name, String description, int idgame) {
        this.id = id;
        this.idgame = idgame;
        this.name = name;
        this.description = description;
        this.timesUsed = 0;
        this.isBought = false;

        this.favorTokenDAO = new FavorTokenDAO();
        this.toolCardDAO = new ToolCardDAO();
    }

    //wordt gebruikt voor Toolcardfactory
    public Toolcard(int idToolcard, String name, String description) {
        this.id = idToolcard;
        this.idgame = 0;
        this.name = name;
        this.description = description;
        this.timesUsed = 0;
        this.isBought = false;

        this.favorTokenDAO = new FavorTokenDAO();
        this.toolCardDAO = new ToolCardDAO();
    }

    public boolean isBought() {
        return isBought;
    }

    @Override
    public int getIdToolcard() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getCost(int idgame) {
        try {
            return favorTokenDAO.getToolCardPrice(toolCardDAO.getGameToolCardId(idgame, getIdToolcard()), idgame);
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public void use() {
        timesUsed++;
    }
}