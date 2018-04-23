package library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataSearch {

    private Filter filter;

    public DataSearch(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
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
            switch (filter) {
                case TITLE:
                    entry.setTitle(rs.getString("title") != null
                                 ? rs.getString("title")
                                 : rs.getString("fileName"));
                    break;
                case FILENAME:
                    entry.setFileName(rs.getString("fileName"));
                    break;
                case ALL:
                    String[] tags = new String[entry.getSize() - 1];
                    for (int i = 0; i < entry.getSize() - 1; i++) {
                        tags[i] = rs.getString(i + 2);
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
        switch (filter) {
            case TITLE:
                data = new String[numberOfRows][1];
                for (int i = 0; i < numberOfRows; i++) {
                    data[i][0] = results.get(i).getTitle();
                }
                break;
            case FILENAME:
                data = new String[numberOfRows][1];
                for (int i = 0; i < numberOfRows; i++) {
                    data[i][0] = results.get(i).getFileName();
                }
                break;
            case ALL:
                data = new String[numberOfRows][6];
                for (int i = 0; i < numberOfRows; i++) {
                    for (int j = 0; j < results.get(i).getSize() - 1; j++) {
                        data[i][j] = results.get(i).getTags()[j + 1];
                    }
                }
        }
        return data;
    }

    public String[][] getResults(SQLConnection con, List<String> query) throws SQLException {
        String conditions = convertToSQL(query);
        ResultSet rs = con.get().createStatement().executeQuery(
                "SELECT * FROM mp3Lib WHERE " + conditions);
        List<DataEntry> results = convertToList(rs);
        return convertToArray(results);
    }

    public enum Filter {
        TITLE, FILENAME, ALL;

        @Override
        public String toString() {
            String s = super.toString();
            return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }
}