package Enumerations;

public enum Colour {
    BLUE("#4287F5"),
    YELLOW("#E3DC09"),
    PURPLE("#7830b3"),
    GREEN("#2FED33"),
    RED("#E30909"),
    WHITE("#FFFFFF");

    private final String hex;

    private Colour(String x) {
        hex = x;
    }

    public String toString() {
        return this.hex;
    }
}

