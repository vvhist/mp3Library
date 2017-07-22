import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class TestMp3Library {
    private static void testFirstArgument() {
        boolean isNoArgs    = Application.firstArgumentIsFolder(new String[] {});
        boolean isNotFolder = Application.firstArgumentIsFolder(new String[] {"String"});
        Assert.assertEquals(isNoArgs,    false);
        Assert.assertEquals(isNotFolder, false);
    }
    private static void testDataCreation() {
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
    private static void testSerialization() throws IOException {
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
    private static void testDeserialization() throws IOException, ClassNotFoundException{
        Database trialDatabase = new Database();
        trialDatabase.data = MusicData.create(new File("test\\resources"));
        Database database = Database.deserialize("test\\resources\\database\\database.ser");
        Assert.assertEquals(database.data, trialDatabase.data);
    }
    private static void testDataSearch() throws IOException, UnsupportedTagException,
            InvalidDataException {
        ArrayList<File> data = MusicData.create(new File("test\\resources"));

        String[] optionValues1 = new String[] {"Year", "2000", "i", "j", "Genre", "Classic Rock"};
        ArrayList<String> results1 = DataSearch.getResults(optionValues1, data);
        ArrayList<String> testResults1 = new ArrayList<>();
        testResults1.add("Five");
        testResults1.add("One");
        testResults1.add("Two");
        testResults1.add("Three");
        testResults1.add("Four");
        testResults1.add("Six");
        Assert.assertEquals(results1, testResults1);

        String[] optionValues2 = new String[] {"Artist", "Artist 2", "Album", "Album 2"};
        ArrayList<String> results2 = DataSearch.getResults(optionValues2, data);
        ArrayList<String> testResults2 = new ArrayList<>();
        testResults2.add("Five");
        testResults2.add("Four");
        testResults2.add("Six");
        Assert.assertEquals(results2, testResults2);
    }
    public static void main(String args[]) throws IOException, ParseException, InvalidDataException,
            UnsupportedTagException, ClassNotFoundException {
        testFirstArgument();
        testDataCreation();
        testSerialization();
        testDeserialization();
        testDataSearch();
        System.out.println();
        System.out.println("---Ignore any messages above---");
        System.out.println("The test has been successfully completed.");
    }
}