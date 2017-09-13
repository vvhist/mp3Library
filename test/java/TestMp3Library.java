import library.Application;
import library.DataSearch;
import library.MusicData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.ResultSet;
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
        searchValues.add("GenreInLowerCase");
        searchValues.add("Classic Rock");
        DataSearch.setQuery(searchValues);
        ResultSet results = DataSearch.getResults("title");
        ArrayList<String> trialResults = new ArrayList<>();
        while (results.next()) {
            trialResults.add(results.getString(1));
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
        searchValues.add("ArtistInLowerCase");
        searchValues.add("Artist 2");
        searchValues.add("AlbumInLowerCase");
        searchValues.add("Album 2");
        DataSearch.setQuery(searchValues);
        results = DataSearch.getResults("title");
        trialResults = new ArrayList<>();
        while (results.next()) {
            trialResults.add(results.getString(1));
        }
        expectedResults = new ArrayList<>();
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        searchValues = new ArrayList<>();
        searchValues.add("TitleInLowerCase");
        searchValues.add("Five");
        DataSearch.setQuery(searchValues);
        results = DataSearch.getResults("fileName");
        trialResults = new ArrayList<>();
        while (results.next()) {
            trialResults.add(results.getString(1));
        }
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(trialResults, expectedResults);
    }
}