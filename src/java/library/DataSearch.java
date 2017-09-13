package library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataSearch {

    private static ArrayList<String> query;

    public static void setQuery(ArrayList<String> query) {
        DataSearch.query = query;
    }

    private static String convertQueryToSQL() {
        for (int i = 1; i < query.size(); i += 2) {
            query.set(i, query.get(i).toLowerCase());
            query.add(i, "='");
            i += 2;
            query.add(i, "' AND ");
        }
        query.set(query.size() - 1, "'");
        return String.join("", query);
    }

    public static ResultSet getResults(String column) throws SQLException {
        String sqlConditions = convertQueryToSQL();
        String sql;
        switch (column) {
            case "fileName":
                sql = "SELECT fileName FROM mp3Lib WHERE ";
                break;
            case "title":
                sql = "SELECT COALESCE (NULLIF (title, ''), fileName) FROM mp3Lib WHERE ";
                break;
            case "all":
                sql = "SELECT fileName, artist, title, album, genre, year FROM mp3Lib WHERE ";
                break;
            default:
                throw new IllegalStateException("Impossible case");
        }
        Statement stmt = Application.getConnection().createStatement();
        return stmt.executeQuery(sql + sqlConditions);
    }
}