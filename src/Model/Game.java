package Model;

import DAL.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Game {

    private int id = 0;
    private int playerID;
    private int turnIdPlayer = 0;
    private int currentRoundId = 1;
    private Timestamp creationDate;
    private List<Die> gameDie;
    private List<Die> availableDie;
    private List<Player> players;
    private List<Patterncard> patterncards;
    private List<PlayerFrameField> playerFrameFields;
    private boolean hasPlacedDie;

    private final PlayerFrameFieldDAO playerFrameFieldDAO = new PlayerFrameFieldDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final GameDAO gameDAO = new GameDAO();
    private final RoundDAO roundDAO = new RoundDAO();
    private List<Public_ObjectiveCard> publicObjectiveCards;
    private Die selectedDie;
    private PublicObjectiveCardDAO publicObjectiveCardDAO = new PublicObjectiveCardDAO();

    public Game() {
        this.creationDate = setCreationDateToCurrentDateTime();
    }

    public Game(int gameid) throws SQLException {
        this.id = gameid;
        this.patterncards = new ArrayList<>();
    }

    public Game(int gameid, int playerID) {
        this.id = gameid;
        this.playerID = playerID;
        this.patterncards = new ArrayList<>();
        initializeGame();
    }

    public Game(int gameid, int playerID, Die selectedDie) {
        this.id = gameid;
        this.playerID = playerID;
        this.patterncards = new ArrayList<>();
        this.selectedDie = selectedDie;
        initializeGame();
    }

    public void initializeGame() {
        GameDAO gameDAO = new GameDAO();
        PlayerDAO playerDAO = new PlayerDAO();
        DieDAO dieDAO = new DieDAO();
        PatterncardDAO patterncardDAO = new PatterncardDAO();

        // Load game basic details
        Game gameDetails = gameDAO.get(id);
        if (gameDetails != null) {
            this.turnIdPlayer = gameDetails.getTurnIdPlayer();
            this.currentRoundId = gameDetails.getCurrentRoundId();
            this.creationDate = gameDetails.getCreationDate();
        }

        // Load all game components
        this.hasPlacedDie = false;
        this.players = playerDAO.loadPlayers(id);
        this.gameDie = dieDAO.loadAllDice(id);
        this.availableDie = dieDAO.loadAvailableDice(id, currentRoundId);
        this.playerFrameFields = playerFrameFieldDAO.loadPlayerFrameFields(id);
        this.publicObjectiveCards = loadPublicObjectiveCards(); // Load public objective cards
        for (Player player : players) {
            Patterncard patterncard = patterncardDAO.loadPatternCardsForPlayers(id, player.getIdPlayer());
            if (patterncard != null) {
                this.patterncards.add(patterncard);
            }
        }
        // Assign dice to squares in player frame fields if applicable
        assignDiceToSquares();
    }

    public String getTurnIdPlayerName() {
        try {
            return playerDAO.get(turnIdPlayer).getUsername();
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Public_ObjectiveCard> loadPublicObjectiveCards() {
        try {
            return publicObjectiveCardDAO.getCardsForGame(getId());
        } catch (SQLException e) {
            //System.err.println("Error loading public objective cards: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void assignDiceToSquares() {
        // Iterate through each player frame field
        for (PlayerFrameField pff : playerFrameFields) {
            if (pff.getDieColor() != null && pff.getDieNumber() != 0) {
                // Find the corresponding pattern card based on the player ID
                Patterncard matchingPatterncard = findPatterncardByPlayerId(pff.getPlayerID());

                if (matchingPatterncard != null) {
                    // Get the square from the pattern card based on coordinates
                    Square square = matchingPatterncard.getSquare(pff.getPositionX(), pff.getPositionY());

                    if (square != null) {
                        // Create a new die based on the frame field's die color and number
                        Die die = new Die(pff.getDieColor(), pff.getDieNumber(), getId());
                        // Assign the die to the square
                        square.setDie(die);
                        //System.out.println("Assigned die to square: " + square.getXPos() + ", " + square.getYPos() + " - " +
                        //        square.getDie().getDieColor() + square.getDie().getEyes());
                    }
                }
            }
        }
    }

    // Helper method to find a pattern card by player ID
    private Patterncard findPatterncardByPlayerId(int playerId) {
        for (Patterncard pc : patterncards) {
            if (pc.getPlayerID() == playerId) {
                return pc;
            }
        }
        return null; // Return null if no matching pattern card is found
    }

    public int getPlayerID() {
        return playerID;
    }

    private Die findDieByNumberAndColor(int dieNumber, String dieColor) {
        for (Die die : gameDie) {
            if (die.getEyes() == dieNumber && die.getDieColor().equals(dieColor)) {
                return die;
            }
        }
        return null; // Return null if no matching die is found
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTurnIdPlayer() {
        return turnIdPlayer;
    }

    public void setTurnIdPlayer(int turnIdPlayer) {
        this.turnIdPlayer = turnIdPlayer;
    }

    public int getCurrentRoundId() {
        return currentRoundId;
    }

    public void setCurrentRoundId(int currentRoundId) {
        this.currentRoundId = currentRoundId;
    }

    public List<Public_ObjectiveCard> getPublicObjectiveCards() {
        return publicObjectiveCards;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    private Timestamp setCreationDateToCurrentDateTime() {
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    public List<Patterncard> getPatterncards() {
        return patterncards;
    }

    public List<Die> getAvailableDie() {
        return availableDie;
    }

    public Die getSelectedDie() {
        return selectedDie;
    }

    public void setSelectedDie(Die selectedDie) {
        this.selectedDie = selectedDie;
    }

    public PlacementResult checkDiePlacement(Square square) {
        if (isFirstDie() && !isEdgeOrCorner(square)) {
            return new PlacementResult(false, "Eerste dobbelsteen moet op een rand of hoek geplaatst worden.");
        }
        if (!isFirstDie() && !isValidAdjacency(square)) {
            return new PlacementResult(false, "Dobbelsteen moet naast een andere liggen, diagonaal of orthogonaal en mag niet dezelfde kleur of nummer hebben.");
        }
        return new PlacementResult(true, "Plaatsing is geldig.");
    }

    public Patterncard getPatternCard(int playerId) {
        for (Patterncard pc : patterncards) {
            if (pc.getPlayerID() == playerId) {
                return pc;
            }
        }
        return null;
    }

    private boolean isFirstDie() {
        Patterncard card = findPatterncardByPlayerId(this.playerID);
        for (Square square : card.getSquares()) {
            if (square.getDie() != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isEdgeOrCorner(Square square) {
        int maxX = Patterncard.COLUMNS;
        int maxY = Patterncard.ROWS;
        return (square.getXPos() == 1 || square.getXPos() == maxX || square.getYPos() == 1 || square.getYPos() == maxY);
    }

    private boolean isValidAdjacency(Square square) {
        boolean hasAdjacent = false;
        int x = square.getXPos();
        int y = square.getYPos();
        Die selectedDie = getSelectedDie();

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Orthogonal directions
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonal directions
        };

        for (int[] dir : directions) {
            int adjX = x + dir[0];
            int adjY = y + dir[1];
            // Adjust bounds check for 1-indexed grid
            if (adjX >= 1 && adjX <= Patterncard.COLUMNS && adjY >= 1 && adjY <= Patterncard.ROWS) {
                Square adjSquare = findSquareByPosition(adjX, adjY);
                if (adjSquare != null && adjSquare.getDie() != null) {
                    if (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) {  // Diagonal adjacency
                        hasAdjacent = true;
                    } else {  // Orthogonal adjacency
                        // Here, we immediately return false if the die has the same color or number
                        if (adjSquare.getDie().getDieColor().equals(selectedDie.getDieColor()) ||
                                adjSquare.getDie().getEyes() == selectedDie.getEyes()) {
                            return false;  // Invalid placement detected
                        }
                        hasAdjacent = true;  // Valid orthogonal adjacency detected
                    }
                }
            }
        }
        return hasAdjacent;  // True if any valid adjacency found, false if invalid placement detected
    }

    // Helper method to find a square by its position
    private Square findSquareByPosition(int x, int y) {
        Patterncard card = findPatterncardByPlayerId(this.playerID);
        if (card != null) {
            return card.getSquare(x, y);
        }

        return null; // If no square is found
    }

    public void placeDieOnPlayerFrameField(Square square) {
        try {
            playerFrameFieldDAO.placeDieOnPlayerFrameField(square, selectedDie, playerID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGameAfterDiePlaced(Square square) {
        updatePlayerFrameField(square, selectedDie);
        updateAvailableDie(selectedDie);
        assignDiceToSquares();
    }

    private void updatePatternCard(Square square, Die selectedDie) {
        for (Patterncard pc : patterncards) {
            if (pc.getPlayerID() == playerID) {
                pc.setDieOnSquare(square, selectedDie);
                //patterncardDAO.savePatternCard(pc);
            }
        }
    }

    private void updateAvailableDie(Die selectedDie) {
        for (Die die : availableDie) {
            if (die.getDieNumber() == selectedDie.getDieNumber() && die.getDieColor().equals(selectedDie.getDieColor())) {
                availableDie.remove(die);
                break;
            }
        }
    }

    private void updatePlayerFrameField(Square square, Die selectedDie) {
        for (PlayerFrameField pff : playerFrameFields) {
            if (pff.getPlayerID() == playerID && pff.getPositionX() == square.getXPos() && pff.getPositionY() == square.getYPos()) {
                pff.setDieNumber(selectedDie.getDieNumber());
                pff.setDieColor(selectedDie.getDieColor());
                //playerFrameFieldDAO.update(pff);
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getNumberOfPlayers() {
        return playerDAO.getNumberOfPlayers(id);
    }

    public int getCurrentRoundNr() {
        return new Round(currentRoundId).getRoundNr();
    }

    public Round getCurrentRound() {
        try {
            return roundDAO.get(getCurrentRoundId());
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean getGameHasEnded() {
        return playerDAO.getPlaystatus(playerID).equals("FINISHED");
    }

    public void setCurrentPlayerID(int idPlayer) {
        this.playerID = idPlayer;
    }

    public boolean getHasPlacedDie() {
        return hasPlacedDie;
    }

    public void setHasPlacedDie(boolean hasPlacedDie) {
        this.hasPlacedDie = hasPlacedDie;
    }
}
