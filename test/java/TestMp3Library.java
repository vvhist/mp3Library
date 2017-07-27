import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class TestMp3Library {
    @Test public void testFirstArgument() {
        boolean isNoArgs    = Application.firstArgumentIsFolder(new String[] {});
        boolean isNotFolder = Application.firstArgumentIsFolder(new String[] {"String"});
        Assert.assertEquals(isNoArgs,    false);
        Assert.assertEquals(isNotFolder, false);
    }
    @Test public void testDataCreation() {
        File folder = new File("test\\resources\\withoutMp3s");
        Assert.assertNull(MusicData.create(folder));

        ArrayList<File>   files = MusicData.create(new File("test\\resources"));
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> trialData = new ArrayList<>();
        trialData.add("ID3v23withSuffixInUPPERCASE.MP3");
        trialData.add("noTags.mp3");
        trialData.add("ID3v1tags.mp3");
        trialData.add("ID3v22tags.mp3");
        trialData.add("ID3v23tags.mp3");
        trialData.add("ID3v24tags.mp3");
        trialData.add("ID3v24tagsSub.mp3");
        for (File file : files) {
            strings.add(file.getName());
        }
        Assert.assertEquals(trialData, strings);
    }
    @Test public void testSerialization() throws IOException {
        File musicFolder = new File("test\\resources");
        String databasePath = musicFolder + File.separator + "database.ser";
        Database.serialize(databasePath, MusicData.create(musicFolder));

        File testDatabase = new File("test\\resources\\database\\database.ser");
        File tempDatabase = new File(databasePath);
        tempDatabase.deleteOnExit();
        byte[] file1 = Files.readAllBytes(testDatabase.toPath());
        byte[] file2 = Files.readAllBytes(tempDatabase.toPath());
        Assert.assertEquals(Arrays.equals(file1, file2), true);
    }
    @Test public void testDeserialization() throws IOException, ClassNotFoundException{
        Database trialDatabase = new Database();
        trialDatabase.data = MusicData.create(new File("test\\resources"));
        Database database = Database.deserialize("test\\resources\\database\\database.ser");
        Assert.assertEquals(database.data, trialDatabase.data);
    }
    @Test public void testDataSearch() throws IOException, UnsupportedTagException,
            InvalidDataException {
        ArrayList<File> data = MusicData.create(new File("test\\resources"));

        String[] optionValues = new String[] {"Year", "2000", "i", "j", "Genre", "Classic Rock"};
        ArrayList<String> results = DataSearch.getResults(optionValues, data);
        ArrayList<String> testResults = new ArrayList<>();
        testResults.add("Five");
        testResults.add("One");
        testResults.add("Two");
        testResults.add("Three");
        testResults.add("Four");
        testResults.add("Six");
        Assert.assertEquals(results, testResults);

        optionValues = new String[] {"Artist", "Artist 2", "Album", "Album 2"};
        results = DataSearch.getResults(optionValues, data);
        testResults = new ArrayList<>();
        testResults.add("Five");
        testResults.add("Four");
        testResults.add("Six");
        Assert.assertEquals(results, testResults);
    }
}