package Model;

public class PlacementResult {
    private boolean isValid;
    private String message;

    public PlacementResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}

