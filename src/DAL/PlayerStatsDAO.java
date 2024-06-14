package DAL;

import Model.PlayerStats;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerStatsDAO {
    public List<PlayerStats> getPlayerStats() throws SQLException {
        List<PlayerStats> stats = new ArrayList<>();
        String query = "SELECT " +
                "u.username, " +
                "COALESCE(g.games_played, 0) AS games_played, " +
                "COALESCE(w.wins, 0) AS wins, " +
                "COALESCE(hs.high_score, 0) AS high_score, " +
                "COALESCE(mc.most_color, '') AS most_color, " +
                "COALESCE(mv.most_value, 0) AS most_value, " +
                "COALESCE(o.opponents_count, 0) AS opponents_count " +
                "FROM " +
                "(SELECT DISTINCT username FROM 2023_sagrada.player) u " +
                "LEFT JOIN " +
                "(SELECT username, COUNT(*) AS games_played " +
                "FROM 2023_sagrada.player " +
                "WHERE playstatus = 'FINISHED' " +
                "GROUP BY username) g ON u.username = g.username " +
                "LEFT JOIN " +
                "(SELECT p.username, COUNT(*) AS wins " +
                "FROM 2023_sagrada.player p " +
                "JOIN ( " +
                "    SELECT idgame, MAX(score) AS max_score " +
                "    FROM 2023_sagrada.player " +
                "    WHERE playstatus = 'FINISHED' " +
                "    GROUP BY idgame " +
                ") max_scores ON p.idgame = max_scores.idgame AND p.score = max_scores.max_score " +
                "GROUP BY p.username) w ON u.username = w.username " +
                "LEFT JOIN " +
                "(SELECT username, MAX(score) AS high_score " +
                "FROM 2023_sagrada.player " +
                "GROUP BY username) hs ON u.username = hs.username " +
                "LEFT JOIN " +
                "(SELECT username, diecolor AS most_color " +
                "FROM ( " +
                "    SELECT p.username, pf.diecolor, " +
                "           ROW_NUMBER() OVER (PARTITION BY p.username ORDER BY COUNT(*) DESC) AS rn " +
                "    FROM 2023_sagrada.playerframefield pf " +
                "    JOIN 2023_sagrada.player p ON p.idplayer = pf.idplayer " +
                "    WHERE pf.diecolor IS NOT NULL " +
                "    GROUP BY p.username, pf.diecolor " +
                ") sub " +
                "WHERE rn = 1) mc ON u.username = mc.username " +
                "LEFT JOIN " +
                "(SELECT username, eyes AS most_value " +
                "FROM ( " +
                "    SELECT p.username, gd.eyes, " +
                "           ROW_NUMBER() OVER (PARTITION BY p.username ORDER BY COUNT(*) DESC) AS rn " +
                "    FROM 2023_sagrada.playerframefield pf " +
                "    JOIN 2023_sagrada.player p ON p.idplayer = pf.idplayer " +
                "    JOIN 2023_sagrada.gamedie gd ON pf.dienumber = gd.dienumber AND pf.diecolor = gd.diecolor AND pf.idgame = gd.idgame " +
                "    WHERE gd.eyes IS NOT NULL " +
                "    GROUP BY p.username, gd.eyes " +
                ") sub " +
                "WHERE rn = 1) mv ON u.username = mv.username " +
                "LEFT JOIN " +
                "(SELECT p.username, COUNT(DISTINCT o.username) AS opponents_count " +
                "FROM 2023_sagrada.player p " +
                "JOIN 2023_sagrada.player o ON p.idgame = o.idgame AND p.username != o.username " +
                "GROUP BY p.username) o ON u.username = o.username " +
                "ORDER BY u.username;";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PlayerStats playerStats = new PlayerStats(
                        rs.getString("username"),
                        rs.getInt("games_played"),
                        rs.getInt("wins"),
                        rs.getInt("high_score"),
                        rs.getString("most_color"),
                        rs.getInt("most_value"),
                        rs.getInt("opponents_count")
                );
                stats.add(playerStats);
            }
        }
        return stats;
    }
}
