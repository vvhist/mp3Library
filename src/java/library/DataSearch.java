package library;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DataSearch {

    private static ColumnFilter filter = ColumnFilter.TITLE;
    private String[][] data;
    private DefaultTableModel tableModel = new DefaultTableModel();

    public DataSearch(Connection con, Map<String, String> searchValues) throws SQLException {
        String sql = "SELECT * FROM mp3Lib WHERE " + buildSQLString(searchValues);
        try (ResultSet rs = con.createStatement().executeQuery(sql)) {
            Log.get().fine(sql);
            createData(convertToList(rs));
        }
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

    private String buildSQLString(Map<String, String> searchValues) {
        searchValues.replaceAll((key, value) -> "'" + value + "'");
        return searchValues.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(" AND "));
    }

    private List<DataEntry> convertToList(ResultSet rs) throws SQLException {
        List<DataEntry> results = new ArrayList<>();

        while (rs.next()) {
            DataEntry entry = new DataEntry();
            switch (filter) {
                case TITLE:
                    entry.setTag(filter, rs.getString(filter.toString()) != null
                                       ? rs.getString(filter.toString())
                                       : rs.getString(ColumnFilter.FILENAME.toString()));
                    break;
                case FILENAME:
                    entry.setTag(filter, rs.getString(filter.toString()));
                    break;
                case ALL:
                    String[] tags = new String[DataEntry.getSize() - 1];
                    for (int i = 0; i < DataEntry.getSize() - 1; i++) {
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
        data = new String[numberOfRows][filter.numberOfColumns];

        switch (filter) {
            case TITLE:
            case FILENAME:
                for (int i = 0; i < numberOfRows; i++) {
                    data[i][0] = results.get(i).getTag(filter);
                }
                break;
            case ALL:
                for (int i = 0; i < numberOfRows; i++) {
                    for (int j = 0; j < DataEntry.getSize() - 1; j++) {
                        data[i][j] = results.get(i).getTags()[j + 1];
                    }
                }
                break;
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

        TITLE   (1),
        FILENAME(1),
        ALL     (6);

        private final int numberOfColumns;

        ColumnFilter(int numberOfColumns) {
            this.numberOfColumns = numberOfColumns;
        }

        @Override
        public String toString() {
            String s = super.toString();
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }
}