import library.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestMp3Library {

    @Test public void testDataSearch() throws SQLException {
        File musicFolder = new File("test/resources");
        File dbLocation = new File(musicFolder, "database/Data");
        Connection con = DriverManager.getConnection("jdbc:hsqldb:file:" + dbLocation, "user", "");
        MusicData.create(con, musicFolder);

        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add("Year");
        searchValues.add("2000");
        searchValues.add("Genre");
        searchValues.add("Classic Rock");
        ArrayList<String> trialResults = DataSearch.getResults(con, searchValues, false);
        ArrayList<String> expectedResults = new ArrayList<>();
        expectedResults.add("One");
        expectedResults.add("Two");
        expectedResults.add("Three");
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.add("Artist");
        searchValues.add("Artist 2");
        searchValues.add("Album");
        searchValues.add("Album 2");
        trialResults = DataSearch.getResults(con, searchValues, false);
        expectedResults = new ArrayList<>();
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.add("Title");
        searchValues.add("Five");
        trialResults = DataSearch.getResults(con, searchValues, true);
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(trialResults, expectedResults);
    }
}