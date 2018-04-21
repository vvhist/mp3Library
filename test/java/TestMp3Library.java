import library.DataSearch;
import library.LibraryData;
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
        LibraryData library = new LibraryData(new File("test/resources"));
        library.create();

        List<String> searchValues = new ArrayList<>();
        searchValues.addAll(Arrays.asList("Year", "2000", "Genre", "Classic Rock"));
        String[][] trialData = new DataSearch("Title").getResults(searchValues);
        List<String> trialResults = new ArrayList<>();
        for (String[] result : trialData) {
            trialResults.add(result[0]);
        }
        List<String> expectedResults = new ArrayList<>();
        expectedResults.addAll(Arrays.asList(
                "One", "Two", "ID3v23tagsWithoutTitle.mp3", "Five", "Four", "Six"));

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.addAll(Arrays.asList("Title", "Five"));
        trialData = new DataSearch("Filename").getResults(searchValues);
        trialResults = new ArrayList<>();
        for (String[] result : trialData) {
            trialResults.add(result[0]);
        }
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.addAll(Arrays.asList("Artist", "Artist 2", "Album", "Album 2"));
        trialData = new DataSearch("all").getResults(searchValues);
        String[][] expectedData = new String[][] {
         {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tags.mp3",                  "Artist 2", "Four", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tagsSub.mp3",               "Artist 2", "Six",  "Album 2", "Classic Rock", "2000"}};

        ArrayAsserts.assertArrayEquals(trialData, expectedData);
        library.delete();
    }
}