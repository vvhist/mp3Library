import library.*;
import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public final class TestMp3Library {

    @Test
    public void testID3TagsRecognition() throws SQLException {
        Map<String, String> searchValues = new HashMap<>();
        searchValues.put("Year", "2000");
        searchValues.put("Genre", "Classic Rock");

        String[][] expectedData = new String[][]{
                {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five", "Album 2", "Classic Rock", "2000"},
                {"ID3v1tags.mp3",                   "Artist 1", "One",  "Album 1", "Classic Rock", "2000"},
                {"ID3v22tags.mp3",                  "Artist 1", "Two",  "Album 1", "Classic Rock", "2000"},
                {"ID3v23tagsWithoutTitle.mp3",      "Artist 1", null,   "Album 1", "Classic Rock", "2000"},
                {"ID3v24tags.mp3",                  "Artist 2", "Four", "Album 2", "Classic Rock", "2000"},
                {"ID3v24tagsSub.mp3",               "Artist 2", "Six",  "Album 2", "Classic Rock", "2000"}
        };
        test(searchValues, expectedData);
    }

    @Test
    public void testSpecificSearch() throws SQLException {
        Map<String, String> searchValues = new HashMap<>();
        searchValues.put("Title", "One");

        String[][] expectedData = new String[][]{
                {"ID3v1tags.mp3", "Artist 1", "One",  "Album 1", "Classic Rock", "2000"}
        };
        test(searchValues, expectedData);
    }

    private void test(Map<String, String> searchValues, String[][] expectedData)
            throws SQLException {
        File musicFolder = new File("test/resources");
        File location = new File(musicFolder, "libraryData");
        Database database = new Database(location);

        List<DataEntry> entries = new AudioData(musicFolder).extract();
        database.create(entries);

        List<DataEntry> results = database.search(searchValues);
        database.delete();

        String[][] actualData = SearchProcessing.createTableData(results);
        ArrayAsserts.assertArrayEquals(actualData, expectedData);
    }
}