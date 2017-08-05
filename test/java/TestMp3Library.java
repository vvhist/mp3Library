import library.Application;
import library.DataSearch;
import library.Database;
import library.MusicData;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TestMp3Library {

    @Test public void testDataCreation() throws IOException, ClassNotFoundException {
        File folder   = new File("test\\resources");
        File location = new File("test\\resources\\database\\database.ser");
        HashMap<String, ArrayList<String>> trialData    = MusicData.create(folder).data;
        HashMap<String, ArrayList<String>> expectedData = Database.deserialize(location).data;

        expectedData.put("FileName", new ArrayList<>());
        expectedData.put("Artist",   new ArrayList<>());
        expectedData.put("Title",    new ArrayList<>());
        expectedData.put("Album",    new ArrayList<>());
        expectedData.put("Genre",    new ArrayList<>());
        expectedData.put("Year",     new ArrayList<>());

        String[] fileName = {"ID3v23withSuffixInUPPERCASE.MP3", "noTags.mp3", "ID3v1tags.mp3",
                             "ID3v22tags.mp3", "ID3v23tags.mp3", "ID3v24tags.mp3",
                             "ID3v24tagsSub.mp3"};
        String[] artist   = {"Artist 2", null, "Artist 1", "Artist 1", "Artist 1", "Artist 2",
                             "Artist 2"};
        String[] title    = {"Five", null, "One", "Two", "Three", "Four", "Six"};
        String[] album    = {"Album 2", null, "Album 1", "Album 1", "Album 1", "Album 2", "Album 2"};
        String[] genre    = {"Classic Rock", null, "Classic Rock", "Classic Rock", "Classic Rock",
                             "Classic Rock", "Classic Rock"};
        String[] year     = {"2000", null, "2000", "2000", "2000", "2000", "2000"};

        Collections.addAll(expectedData.get("FileName"), fileName);
        Collections.addAll(expectedData.get("Artist"),   artist);
        Collections.addAll(expectedData.get("Title"),    title);
        Collections.addAll(expectedData.get("Album"),    album);
        Collections.addAll(expectedData.get("Genre"),    genre);
        Collections.addAll(expectedData.get("Year"),     year);

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

        Database expectedDatabase = new Database();
        expectedDatabase.data = MusicData.create(new File("test\\resources")).data;

        Assert.assertEquals(trialDatabase.data, expectedDatabase.data);
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