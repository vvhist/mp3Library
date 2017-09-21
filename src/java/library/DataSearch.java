package library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataSearch {

    private static String column;

    public static void setColumn(String column) {
        DataSearch.column = column;
    }

    private static String convertToSQL(ArrayList<String> query) {
        for (int i = 1; i < query.size(); i += 2) {
            query.add(i, "='");
            i += 2;
            query.add(i, "' AND ");
        }
        query.set(query.size() - 1, "'");
        return String.join("", query);
    }

    private static ArrayList<DataEntry> convertToList(ResultSet rs) throws SQLException {
        ArrayList<DataEntry> results = new ArrayList<>();
        while (rs.next()) {
            DataEntry entry = new DataEntry();
            switch (column) {
                case "Title":
                    entry.setTitle(rs.getString("title") != null
                                 ? rs.getString("title")
                                 : rs.getString("fileName"));
                    break;
                case "Filename":
                    entry.setFileName(rs.getString("fileName"));
                    break;
                case "all":
                    entry.setFileName(rs.getString("fileName"));
                    entry.setArtist(rs.getString("artist"));
                    entry.setTitle(rs.getString("title"));
                    entry.setAlbum(rs.getString("album"));
                    entry.setGenre(rs.getString("genre"));
                    entry.setYear(rs.getString("year"));
                    break;
            }
            results.add(entry);
        }
        return results;
    }

    private static String[][] convertToArray(ArrayList<DataEntry> results) {
        String[][] data = new String[][]{};
        int numberOfRows = results.size();
        switch (column) {
            case "Title":
                data = new String[numberOfRows][1];
                for (int i = 0; i < numberOfRows; i++) {
                    data[i][0] = results.get(i).getTitle();
                }
                break;
            case "Filename":
                data = new String[numberOfRows][1];
                for (int i = 0; i < numberOfRows; i++) {
                    data[i][0] = results.get(i).getFileName();
                }
                break;
            case "all":
                data = new String[numberOfRows][6];
                for (int i = 0; i < numberOfRows; i++) {
                    data[i][0] = results.get(i).getFileName();
                    data[i][1] = results.get(i).getArtist();
                    data[i][2] = results.get(i).getTitle();
                    data[i][3] = results.get(i).getAlbum();
                    data[i][4] = results.get(i).getGenre();
                    data[i][5] = results.get(i).getYear();
                }
        }
        return data;
    }

    public static String[][] getResults(ArrayList<String> query) throws SQLException {
        Statement stmt = Application.getConnection().createStatement();
        String conditions = convertToSQL(query);
        ResultSet rs = stmt.executeQuery("SELECT * FROM mp3Lib WHERE " + conditions);
        ArrayList<DataEntry> results = convertToList(rs);
        return convertToArray(results);
    }
}