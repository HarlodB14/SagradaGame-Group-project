package Controller;

import DAL.*;
import Model.Account;
import Model.Game;
import Model.PlayStatus;
import Model.Player;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CreategameController {

    private final GameDAO gameDAO;
    private final DieDAO dieDAO;
    private final ToolCardDAO toolCardDAO;
    private final AccountDAO accountDAO;
    private final PublicObjectiveCardDAO publicObjectiveCardDAO;
    private final PlayerDAO playerDAO;
    private final FavorTokenDAO favorTokenDAO;
    private final PlayerFrameFieldDAO playerFrameFieldDAO;
    private final PatterncardoptionDAO patterncardoptionDAO;
    private final ColorDAO colorDAO;

    private String privateObjectiveColor;

    public CreategameController() {
        this.gameDAO = new GameDAO();
        this.dieDAO = new DieDAO();
        this.toolCardDAO = new ToolCardDAO();
        this.accountDAO = new AccountDAO();
        this.publicObjectiveCardDAO = new PublicObjectiveCardDAO();
        this.playerDAO = new PlayerDAO();
        this.favorTokenDAO = new FavorTokenDAO();
        this.playerFrameFieldDAO = new PlayerFrameFieldDAO();
        this.patterncardoptionDAO = new PatterncardoptionDAO();
        this.colorDAO = new ColorDAO();
    }

    public void createGame(ArrayList<String> invitedPlayerNames) throws SQLException {
        Game game = new Game();

        // Insert a row into the game table
        int gameId = gameDAO.insert(game);
        game.setId(gameId);

        // Insert the 90 dice rows into the gamedie table
        dieDAO.insert(gameId);
        dieDAO.giveDieGameIDBasedOnNumberOfPlayers(gameId, (invitedPlayerNames.size() + 1));

        // Select 3 random tool cards and insert them
        toolCardDAO.insert(gameId);

        // Select 3 random public objective cards and insert them
        publicObjectiveCardDAO.insert(gameId);

        // Set up the players
        int seqnr = 2;
        List<String> colors = colorDAO.getAll();
        Collections.shuffle(colors);
        Collections.shuffle(invitedPlayerNames);

        // Insert the challenger
        Player challenger = new Player();
        challenger.setUsername(ApplicationController.currentLoggedInAccount.getUsername());
        challenger.setIdGame(gameId);
        challenger.setPlayStatus(PlayStatus.CHALLENGER.toString());
        challenger.setSeqNr(1);
        setPrivateObjectiveColor(colors);
        challenger.setPrivateObjectiveCardColor(privateObjectiveColor);
        challenger.setIdPatternCard(0);
        challenger.setScore(0);
        int playerChallengerId = playerDAO.insert(challenger);

        // Insert the challengees
        for (String playerName : invitedPlayerNames) {
            Player player = new Player();
            player.setUsername(playerName);
            player.setIdGame(gameId);
            player.setPlayStatus(PlayStatus.CHALLENGEE.toString());
            player.setSeqNr(seqnr);
            seqnr++;
            setPrivateObjectiveColor(colors);
            player.setPrivateObjectiveCardColor(privateObjectiveColor);
            player.setIdPatternCard(0);
            player.setScore(0);
            playerDAO.insert(player);
        }

        // Insert the favor tokens
        favorTokenDAO.insert(gameId);

        // Insert the player frame fields
        playerFrameFieldDAO.insert(gameId);

        // Insert the pattern card choices
//        patterncardoptionDAO.insert(gameId);

        // Update turn in game row
        game.setTurnIdPlayer(playerChallengerId);
        gameDAO.update(game);
    }

    public List<String> getAllAccountNames() throws SQLException {
        List<Account> allAccounts = accountDAO.getAll();

        ArrayList<String> allAccountNames = new ArrayList<>();

        for (Account account : allAccounts) {
            if (!Objects.equals(ApplicationController.currentLoggedInAccount.getUsername().toLowerCase(), account.getUsername().toLowerCase())) {
                allAccountNames.add(account.getUsername());
            }
        }

        return allAccountNames;
    }

    private void setPrivateObjectiveColor(List<String> colors) {
        if (colors != null && !colors.isEmpty()) {
            this.privateObjectiveColor = colors.get(0);
            colors.remove(0);
        }
    }

    public boolean canInvitePlayers(ArrayList<String> accountNames) {
        for (String username : accountNames) {
            if (playerDAO.canInvitePlayer(username, ApplicationController.currentLoggedInAccount.getUsername())) {
                ApplicationController.popupMessage("Kan " + username + " niet uitnodigen!", "Kan " + username + " niet uitnodigen!", "Je kan geen spelers uitnodigen die nog een openstaande uitnodiging van jou hebben!", Alert.AlertType.ERROR);
                return false;
            }
        }
        return true;
    }
}
