package library;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSearch {

    private static ColumnFilter filter = ColumnFilter.TITLE;
    private String[][] data = new String[][]{};
    private DefaultTableModel tableModel = new DefaultTableModel();

    public DataSearch(SQLConnection con, List<String> searchValues) throws SQLException {
        ResultSet rs = con.get().createStatement().executeQuery(
                "SELECT * FROM mp3Lib WHERE " + addSQLSyntax(searchValues));
        createData(convertToList(rs));
        createTableModel();
    }

    public static void setFilter(ColumnFilter filter) {
        DataSearch.filter = filter;
    }

    public String[][] getData() {
        return data;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    private static String addSQLSyntax(List<String> query) {
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
                    entry.setTitle(rs.getString(filter.toString()) != null
                                 ? rs.getString(filter.toString())
                                 : rs.getString(ColumnFilter.FILENAME.toString()));
                    break;
                case FILENAME:
                    entry.setFileName(rs.getString(filter.toString()));
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

    private void createData(List<DataEntry> results) {
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
    }

    private void createTableModel() {
        Object[] columnNames;
        if (filter == ColumnFilter.ALL) {
            String[] tagNames = DataEntry.getTagNames();
            columnNames = Arrays.copyOfRange(tagNames, 1, tagNames.length);
        } else {
            columnNames = new String[] {filter.toString()};
        }
        tableModel.setDataVector(data, columnNames);
    }

    public enum ColumnFilter {
        TITLE, FILENAME, ALL;

        @Override
        public String toString() {
            String s = super.toString();
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }
}