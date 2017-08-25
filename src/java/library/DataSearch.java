package library;

import org.apache.commons.cli.CommandLine;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class DataSearch {

    private static ArrayList<String> removeNonExistingKeys(Connection con, CommandLine cmd)
            throws SQLException {

        ArrayList<String> searchQuery = new ArrayList<>();
        Collections.addAll(searchQuery, cmd.getOptionValues("search"));
        DatabaseMetaData md = con.getMetaData();
        for (int i = 0; i < searchQuery.size() - 1; i += 2) {
            ResultSet rs = md.getColumns(null, null, "MP3LIB",
                    searchQuery.get(i).toUpperCase());
            if (!rs.next()) {
                searchQuery.remove(i);
                searchQuery.remove(i);
            }
        }
        return searchQuery;
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

    public static ArrayList<String> getResults(Connection con, CommandLine cmd) throws SQLException {
        ArrayList<String> searchQuery = removeNonExistingKeys(con, cmd);
        String conditions = convertToSQL(searchQuery);

        if (cmd.hasOption("path")) {
            String sql = "SELECT fileName FROM mp3Lib WHERE " + conditions;
            return collectResults(con, sql, "fileName");
        } else {
            ArrayList<String> selectedData = new ArrayList<>();

            String sql = "SELECT title FROM mp3Lib WHERE " + conditions + " AND title IS NOT NULL";
            selectedData.addAll(collectResults(con, sql, "title"));

            sql = "SELECT fileName FROM mp3Lib WHERE " + conditions + " AND title IS NULL";
            selectedData.addAll(collectResults(con, sql, "fileName"));
            return selectedData;
        }
    }
}