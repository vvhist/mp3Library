package library;

import java.util.List;

public final class SearchProcessing {

    private SearchProcessing() {}

    public static String[][] createTableData(List<DataEntry> results) {
        int numberOfRows = results.size();
        int numberOfColumns = DataEntry.getSize();
        String[][] data = new String[numberOfRows][numberOfColumns];

        for (int i = 0; i < numberOfRows; i++) {
            data[i][0] = results.get(i).getFilename();
            data[i][1] = results.get(i).getArtist();
            data[i][2] = results.get(i).getTitle();
            data[i][3] = results.get(i).getAlbum();
            data[i][4] = results.get(i).getGenre();
            Integer year = results.get(i).getYear();
            data[i][5] = (year == null) ? "" : String.valueOf(year);
        }
        return data;
    }
}