import library.DataSearch;
import library.LibraryData;
import library.SQLConnection;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMp3Library {

    @Test public void testDataSearch() throws SQLException {
        SQLConnection con = new SQLConnection(new File("test/resources"));
        LibraryData library = new LibraryData(con);
        library.create();

        List<String> searchValues = new ArrayList<>();
        searchValues.addAll(Arrays.asList("Year", "2000", "Genre", "Classic Rock"));
        String[][] actualData = new DataSearch(DataSearch.Filter.TITLE).getResults(con, searchValues);
        List<String> actualResults = new ArrayList<>();
        for (String[] result : actualData) {
            actualResults.add(result[0]);
        }
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(Arrays.asList(
                "Five", "One", "Two", "ID3v23tagsWithoutTitle.mp3", "Four", "Six"));

        Assert.assertEquals(actualResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.addAll(Arrays.asList("Title", "Five"));
        actualData = new DataSearch(DataSearch.Filter.FILENAME).getResults(con, searchValues);
        actualResults = new ArrayList<>();
        for (String[] result : actualData) {
            actualResults.add(result[0]);
        }
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(actualResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.addAll(Arrays.asList("Artist", "Artist 2", "Album", "Album 2"));
        actualData = new DataSearch(DataSearch.Filter.ALL).getResults(con, searchValues);
        String[][] expectedData = new String[][] {
         {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tags.mp3",                  "Artist 2", "Four", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tagsSub.mp3",               "Artist 2", "Six",  "Album 2", "Classic Rock", "2000"}};

        ArrayAsserts.assertArrayEquals(actualData, expectedData);
        library.delete();
    }
}