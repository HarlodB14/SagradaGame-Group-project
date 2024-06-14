package Controller;

import DAL.PatterncardDAO;
import DAL.PlayerDAO;
import Model.Patterncard;
import Model.Player;
import Model.Square;

import java.sql.SQLException;
import java.util.*;

public class PatternCardController {

    private Patterncard patterncard;
    private PatterncardDAO patterncardDAO;
    private int patternCardId;

    public PatternCardController() {
        patterncardDAO = new PatterncardDAO();
        patterncard = new Patterncard(patternCardId);
    }
    
    public PatternCardController(int patternCardID, int playerID, String username, String name, int difficulty, Square[] squares) throws SQLException {
        patterncard = new Patterncard(patternCardID, playerID, username, name, difficulty, squares);
    }

    public Patterncard getSelectedPatternCard(int id) throws SQLException {
        return patterncardDAO.get(id);
    }

    public List<Patterncard> getPatternCardOptions(int playerId, int gameId) throws SQLException {
        PlayerDAO playerDAO = new PlayerDAO();
        List<Patterncard> allPatternCards = patterncardDAO.getAll();
        List<Player> playersInGame = playerDAO.getPlayersByGameId(gameId);
        List<Patterncard> patternCardsToAssign = new ArrayList<>();
        // Kopie van alles om te manipuleren
        List<Patterncard> availablePatternCards = new ArrayList<>(allPatternCards);

        // Shuffle om iedereen random kaarten te geven
        Collections.shuffle(availablePatternCards);

        boolean playerHasOption = patterncardDAO.playerHasOption(playerId);
        //check eerst of current player al opties heeft, zowel laat ze meteen zien
        if (playerHasOption) {
            return patterncardDAO.getPatternCardOptionsForPlayer(playerId);
        }

        for (Player player : playersInGame) {
            // Reset voor elke speler
            patternCardsToAssign.clear();
            for (int i = 0; i < 4; i++) {
                if (!availablePatternCards.isEmpty()) {
                    Patterncard patterncard = availablePatternCards.get(i);
                    patternCardsToAssign.add(patterncard);
                    availablePatternCards.remove(patterncard);
                } else {
                    break;
                }
            }
            //toevoegen in options tabel
            patterncardDAO.insertPatternCardOptions(patternCardsToAssign, player.getIdPlayer());
        }

        // teruggeven voor in de view te tonen
        return patterncardDAO.getPatternCardOptionsForPlayer(playerId);
    }

    public Patterncard getPatternCard() {
        return patterncard;
    }
}
