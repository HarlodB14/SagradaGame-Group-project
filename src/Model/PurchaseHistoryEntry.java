package Model;

public class PurchaseHistoryEntry {

    private int playerId;
    private int roundId;
    private int tokens;

    public PurchaseHistoryEntry(int playerId, int roundId, int tokens) {
        this.playerId = playerId;
        this.roundId = roundId;
        this.tokens = tokens;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getRoundId() {
        return roundId;
    }

    public int getTokens() {
        return tokens;
    }

}
