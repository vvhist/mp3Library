import library.Application;
import library.DataSearch;
import library.MusicData;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestMp3Library {

    @Test public void testDataSearch() throws SQLException {
        MusicData.setMusicFolder(new File("test/resources"));
        Application.setConnection();
        MusicData.create();

        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add("Year");
        searchValues.add("2000");
        searchValues.add("Genre");
        searchValues.add("Classic Rock");
        String[][] trialData = DataSearch.getResults("Title", searchValues);
        ArrayList<String> trialResults = new ArrayList<>();
        for (String[] result : trialData) {
            trialResults.add(result[0]);
        }
        ArrayList<String> expectedResults = new ArrayList<>();
        expectedResults.add("One");
        expectedResults.add("Two");
        expectedResults.add("Three");
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.add("Title");
        searchValues.add("Five");
        trialData = DataSearch.getResults("Filename", searchValues);
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
        trialData = DataSearch.getResults("all", searchValues);
        String[][] expectedData = new String[][] {
         {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tags.mp3",                  "Artist 2", "Four", "Album 2", "Classic Rock", "2000"},
         {"ID3v24tagsSub.mp3",               "Artist 2", "Six",  "Album 2", "Classic Rock", "2000"}};

        ArrayAsserts.assertArrayEquals(trialData, expectedData);
    }
}