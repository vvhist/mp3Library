import library.DataSearch;
import library.LibraryData;
import library.Log;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMp3Library {

    @Test public void testDataSearch() throws SQLException {
        File DatabaseLocation = new File(new File("test/resources"), "libraryData");
        LibraryData library = new LibraryData(DatabaseLocation);
        String url = "jdbc:hsqldb:file:" + new File(DatabaseLocation, "Data");
        try (Connection con = DriverManager.getConnection(url, "user", "");
             Statement stmt = con.createStatement()) {
            Log.get().info("Connection is established to " + url);
            library.create(con);

            List<String> searchValues = new ArrayList<>(Arrays.asList("Year",  "2000",
                                                                      "Genre", "Classic Rock"));
            DataSearch.setFilter(DataSearch.ColumnFilter.TITLE);
            String[][] actualData = new DataSearch(con, searchValues).getData();
            List<String> actualResults = new ArrayList<>();
            for (String[] result : actualData) {
                actualResults.add(result[0]);
            }
            List<String> expectedResults = new ArrayList<>(Arrays.asList(
                    "Five", "One", "Two", "ID3v23tagsWithoutTitle.mp3", "Four", "Six"));

            Assert.assertEquals(actualResults, expectedResults);

            searchValues = new ArrayList<>(Arrays.asList("Title", "Five"));
            DataSearch.setFilter(DataSearch.ColumnFilter.FILENAME);
            actualData = new DataSearch(con, searchValues).getData();
            actualResults = new ArrayList<>();
            for (String[] result : actualData) {
                actualResults.add(result[0]);
            }
            expectedResults = new ArrayList<>();
            expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

            Assert.assertEquals(actualResults, expectedResults);

            searchValues = new ArrayList<>(Arrays.asList("Artist", "Artist 2",
                                                         "Album",  "Album 2"));
            DataSearch.setFilter(DataSearch.ColumnFilter.ALL);
            actualData = new DataSearch(con, searchValues).getData();

            String[][] expectedData = new String[][]{
                    {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five", "Album 2", "Classic Rock", "2000"},
                    {"ID3v24tags.mp3",                  "Artist 2", "Four", "Album 2", "Classic Rock", "2000"},
                    {"ID3v24tagsSub.mp3",               "Artist 2", "Six",  "Album 2", "Classic Rock", "2000"}};

            ArrayAsserts.assertArrayEquals(actualData, expectedData);
            library.delete(stmt);
        }
    }
}