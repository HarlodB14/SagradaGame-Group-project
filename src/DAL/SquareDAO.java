package DAL;

import Enumerations.Colour;
import Model.Square;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SquareDAO {

    public void loadProperties(Square square) throws SQLException {
        try (PreparedStatement preparedStatement = DatabaseConnector.getConnection().prepareStatement(
                "SELECT value, color FROM patterncardfield WHERE idpatterncard = ? AND position_x = ? AND position_y = ?")) {
            preparedStatement.setInt(1, square.getPatternCardID());
            preparedStatement.setInt(2, square.getXPos());
            preparedStatement.setInt(3, square.getYPos());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int value = resultSet.getInt("value");
                    String color = resultSet.getString("color");
                    if (value != 0) {
                        square.setEyes(value);
                    }
                    if (color != null) {
                        square.setColor(Colour.valueOf(color.toUpperCase()));
                        square.setColorString(color.toUpperCase());
                    }
                }
            }
        }
    }

//    public void checkForDice(Square square) throws SQLException {
//        try (Connection connection = DatabaseConnector.makeDatabaseConnection();
//             PreparedStatement stmt = connection.prepareStatement(
//                     "SELECT diecolor, eyes FROM gamedie WHERE idgame = ? AND dienumber = ?")) {
//            stmt.setInt(1, Game.currentGame.getId());
//            stmt.setInt(2, square.getDie() != null ? square.getDie().getDieNumber() : -1);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                String dieColor = rs.getString("diecolor");
//                int eyes = rs.getInt("eyes");
//                square.setDie(new Die(Colour.valueOf(dieColor.toUpperCase()), eyes, square.getDie().getDieNumber()));
//            }
//        }
//    }
//
//    public void saveSquare(Square square) throws SQLException {
//        try (Connection connection = DatabaseConnector.makeDatabaseConnection()) {
//            PreparedStatement stmt = connection.prepareStatement(
//                    "UPDATE patterncardfield SET diecolor = ?, eyes = ? WHERE idpatterncard = ? AND position_x = ? AND position_y = ?");
//            stmt.setString(1, square.getDie().getColour().toString());
//            stmt.setInt(2, square.getDie().getEye());
//            stmt.setInt(3, square.getPatternCardID());
//            stmt.setInt(4, square.getXPos());
//            stmt.setInt(5, square.getYPos());
//            stmt.executeUpdate();
//        }
//    }
}
