package library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataSearch {

    private final String column;

    public DataSearch(String column) {
        this.column = column;
    }

    private static String convertToSQL(List<String> query) {
        for (int i = 1; i < query.size(); i += 2) {
            query.add(i, "='");
            i += 2;
            query.add(i, "' AND ");
        }
        query.set(query.size() - 1, "'");
        return String.join("", query);
    }

    private List<DataEntry> convertToList(ResultSet rs) throws SQLException {
        List<DataEntry> results = new ArrayList<>();
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
                    String[] tags = new String[entry.getSize()];
                    for (int i = 0; i < entry.getSize(); i++) {
                        tags[i] = rs.getString(i + 1);
                    }
                    entry.setTags(tags);
                    break;
            }
            results.add(entry);
        }
        return results;
    }

    private String[][] convertToArray(List<DataEntry> results) {
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
                    for (int j = 0; j < results.get(i).getSize(); j++) {
                        data[i][j] = results.get(i).getTags()[j];
                    }
                }
        }
        return data;
    }

    public String[][] getResults(List<String> query) throws SQLException {
        String conditions = convertToSQL(query);
        ResultSet rs = LibraryData.getSQLStatement().executeQuery(
                "SELECT * FROM mp3Lib WHERE " + conditions);
        List<DataEntry> results = convertToList(rs);
        return convertToArray(results);
    }
}