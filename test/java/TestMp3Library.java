import library.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class TestMp3Library {

    private static TagEntry setTags(String[] tags) {
        TagEntry entry = new TagEntry();
        entry.setTag("FileName", tags[0]);
        entry.setTag("Artist",   tags[1]);
        entry.setTag("Title",    tags[2]);
        entry.setTag("Album",    tags[3]);
        entry.setTag("Genre",    tags[4]);
        entry.setTag("Year",     tags[5]);
        return entry;
    }

    @Test public void testDataCreation() throws IOException, ClassNotFoundException {
        File folder = new File("test\\resources");
        ArrayList<TagEntry> trialData    = MusicData.create(folder).getData();
        ArrayList<TagEntry> expectedData = new ArrayList<>();

        String[] tags = new String[] {"ID3v23withSuffixInUPPERCASE.MP3", "Artist 2", "Five",
                                      "Album 2", "Classic Rock", "2000"};
        expectedData.add(setTags(tags));

        tags = new String[] {"noTags.mp3", null, null, null, null, null};
        expectedData.add(setTags(tags));

        tags = new String[] {"ID3v1tags.mp3", "Artist 1", "One", "Album 1", "Classic Rock", "2000"};
        expectedData.add(setTags(tags));

        tags = new String[] {"ID3v22tags.mp3", "Artist 1", "Two", "Album 1", "Classic Rock", "2000"};
        expectedData.add(setTags(tags));

        tags = new String[] {"ID3v23tags.mp3", "Artist 1", "Three", "Album 1", "Classic Rock",
                             "2000"};
        expectedData.add(setTags(tags));

        tags = new String[] {"ID3v24tags.mp3", "Artist 2", "Four", "Album 2", "Classic Rock",
                             "2000"};
        expectedData.add(setTags(tags));

        tags = new String[] {"ID3v24tagsSub.mp3", "Artist 2", "Six", "Album 2", "Classic Rock",
                             "2000"};
        expectedData.add(setTags(tags));

        Assert.assertEquals(trialData, expectedData);
    }

    @Test public void testSerialization() throws IOException {
        File musicFolder = new File("test\\resources");
        File databaseLocation = new File(musicFolder, "database.ser");
        Database database = MusicData.create(musicFolder);
        Database.serialize(database, databaseLocation);

        File trialDatabase    = new File(databaseLocation.getPath());
        trialDatabase.deleteOnExit();
        File expectedDatabase = new File("test\\resources\\database\\database.ser");

        byte[] trial    = Files.readAllBytes(trialDatabase   .toPath());
        byte[] expected = Files.readAllBytes(expectedDatabase.toPath());

        Assert.assertEquals(trial, expected);
    }

    @Test public void testDeserialization() throws IOException, ClassNotFoundException {
        File location = new File("test\\resources\\database\\database.ser");
        Database trialDatabase = Database.deserialize(location);
        Database expectedDatabase = MusicData.create(new File("test\\resources"));

        Assert.assertEquals(trialDatabase.getData(), expectedDatabase.getData());
    }

    @Test public void testDataSearch() throws ParseException {
        String[] args = new String[] {"test\\resources", "--search", "Year=2000", "i=j",
                                      "Genre=Classic Rock"};
        CommandLine cmd = Application.createCommandLineOptions(args);
        Database database = MusicData.create(new File(args[0]));

        ArrayList<Object> trialResults = DataSearch.getResults(database, cmd);
        ArrayList<String> expectedResults = new ArrayList<>();
        expectedResults.add("Five");
        expectedResults.add("One");
        expectedResults.add("Two");
        expectedResults.add("Three");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        args = new String[] {"test\\resources", "--search", "Artist=Artist 2", "Album=Album 2"};
        cmd = Application.createCommandLineOptions(args);
        database = MusicData.create(new File(args[0]));

        trialResults = DataSearch.getResults(database, cmd);
        expectedResults = new ArrayList<>();
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        args = new String[] {"test\\resources", "--search", "Title=Five", "--path"};
        cmd = Application.createCommandLineOptions(args);
        database = MusicData.create(new File(args[0]));

        trialResults = DataSearch.getResults(database, cmd);
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(trialResults, expectedResults);
    }
}