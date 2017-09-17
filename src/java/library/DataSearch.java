package library;

import java.sql.Connection;
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

    private static String[][] convertToArray(ResultSet rs) throws SQLException {
        rs.last();
        int numberOfRows = rs.getRow();
        rs.beforeFirst();
        int numberOfColumns = rs.getMetaData().getColumnCount();
        String[][] data = new String[numberOfRows][numberOfColumns];
        int i = 0;
        while (rs.next() && i < numberOfRows) {
            for (int j = 0; j < numberOfColumns; j++) {
                data[i][j] = rs.getString(j + 1);
            }
            i++;
        }
        return data;
    }

    public static String[][] getResults(String column) throws SQLException {
        switch (column) {
            case "Title":
                column = "COALESCE (NULLIF (title, ''), fileName)";
                break;
            case "all":
                column = "*";
                break;
        }
        Connection con = Application.getConnection();
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                             ResultSet.CONCUR_READ_ONLY);
        String conditions = convertQueryToSQL();
        ResultSet rs = stmt.executeQuery("SELECT " + column + " FROM mp3Lib WHERE " + conditions);
        return convertToArray(rs);
    }
}