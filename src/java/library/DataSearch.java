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
            query.add(i, "='");
            i += 2;
            query.add(i, "' AND ");
        }
        query.set(query.size() - 1, "'");
        return String.join("", query);
    }

    public static ResultSet getResults(String column) throws SQLException {
        switch (column) {
            case "title":
                column = "COALESCE (NULLIF (title, ''), fileName)";
                break;
            case "all":
                column = "*";
                break;
        }
        Statement stmt = Application.getConnection().createStatement();
        String conditions = convertQueryToSQL();
        return stmt.executeQuery("SELECT " + column + " FROM mp3Lib WHERE " + conditions);
    }
}