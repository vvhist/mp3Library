import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import library.Application;
import library.DataSearch;
import library.LibraryData;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestMp3Library {

    @Test public void testDataSearch()
            throws SQLException, IOException, UnsupportedTagException, InvalidDataException {
        LibraryData.setMusicFolder(new File("test/resources"));
        Application.setConnection();
        LibraryData.create();

        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add("Year");
        searchValues.add("2000");
        searchValues.add("Genre");
        searchValues.add("Classic Rock");
        DataSearch.setColumn("Title");
        String[][] trialData = DataSearch.getResults(searchValues);
        ArrayList<String> trialResults = new ArrayList<>();
        for (String[] result : trialData) {
            trialResults.add(result[0]);
        }
        ArrayList<String> expectedResults = new ArrayList<>();
        expectedResults.add("One");
        expectedResults.add("Two");
        expectedResults.add("ID3v23tagsWithoutTitle.mp3");
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.add("Title");
        searchValues.add("Five");
        DataSearch.setColumn("Filename");
        trialData = DataSearch.getResults(searchValues);
        trialResults = new ArrayList<>();
        for (String[] result : trialData) {
            trialResults.add(result[0]);
        }
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.add("Artist");
        searchValues.add("Artist 2");
        searchValues.add("Album");
        searchValues.add("Album 2");
        DataSearch.setColumn("all");
        trialData = DataSearch.getResults(searchValues);
        String[][] expectedData = new String[][] {
         {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tags.mp3",                  "Artist 2", "Four", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tagsSub.mp3",               "Artist 2", "Six",  "Album 2", "Classic Rock", "2000"}};

        ArrayAsserts.assertArrayEquals(trialData, expectedData);
    }
}