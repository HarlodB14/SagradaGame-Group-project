package Model;

import java.sql.Timestamp;

public class Chat {
    private int idPlayer;
    private Timestamp time;
    private String message;

    public Chat(int idPlayer, Timestamp time, String message) {
        this.idPlayer = idPlayer;
        this.time = time;
        this.message = message;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
