package library;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataSearch {

    private static void convertValuesToLowercase(ArrayList<String> searchQuery) {
        for (int i = 1; i < searchQuery.size(); i +=2) {
            searchQuery.set(i, searchQuery.get(i).toLowerCase());
        }
    }

    private static String convertToSQL(ArrayList<String> searchQuery) {
        for (int i = 1; i < searchQuery.size(); i += 2) {
            searchQuery.add(i, "='");
            i += 2;
            searchQuery.add(i, "' AND ");
        }
        searchQuery.set(searchQuery.size() - 1, "'");
        return String.join("", searchQuery);
    }

    private static ArrayList<String> collectResults(Connection con, String sql, String column)
            throws SQLException {

        ArrayList<String> selectedData = new ArrayList<>();
        Statement stmt = con.createStatement();
        ResultSet results = stmt.executeQuery(sql);
        while (results.next()) {
            selectedData.add(results.getString(column));
        }
        return selectedData;
    }

    public static ArrayList<String> getResults(Connection con, ArrayList<String> searchQuery,
                                               boolean isFileNameNeeded) throws SQLException {
        convertValuesToLowercase(searchQuery);
        String conditions = convertToSQL(searchQuery);

        if (isFileNameNeeded) {
            String sql = "SELECT fileName FROM mp3Lib WHERE " + conditions;
            return collectResults(con, sql, "fileName");
        } else {
            ArrayList<String> selectedData = new ArrayList<>();

            String sql = "SELECT titleInMixedCase FROM mp3Lib WHERE " + conditions
                    + " AND title IS NOT NULL";
            selectedData.addAll(collectResults(con, sql, "titleInMixedCase"));

            sql = "SELECT fileName FROM mp3Lib WHERE " + conditions + " AND title IS NULL";
            selectedData.addAll(collectResults(con, sql, "fileName"));
            return selectedData;
        }
    }
}