import library.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestMp3Library {

    @Test public void testDataSearch() throws ParseException, SQLException {
        String url = "jdbc:hsqldb:file:C:/Gradle/mp3Library/test/resources/database/Data";
        Connection con = DriverManager.getConnection(url, "user", "");
        MusicData.create(con, new File("test/resources"));

        String[] args = new String[] {"test/resources", "--search", "Year=2000", "i=j",
                                      "Genre=Classic Rock"};
        CommandLine cmd = Application.createCommandLineOptions(args);

        ArrayList<String> trialResults = DataSearch.getResults(con, cmd);
        ArrayList<String> expectedResults = new ArrayList<>();
        expectedResults.add("One");
        expectedResults.add("Two");
        expectedResults.add("Three");
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        args = new String[] {"test/resources", "--search", "Artist=Artist 2", "Album=Album 2"};
        cmd = Application.createCommandLineOptions(args);

        trialResults = DataSearch.getResults(con, cmd);
        expectedResults = new ArrayList<>();
        expectedResults.add("Five");
        expectedResults.add("Four");
        expectedResults.add("Six");

        Assert.assertEquals(trialResults, expectedResults);

        args = new String[] {"test/resources", "--search", "Title=Five", "--path"};
        cmd = Application.createCommandLineOptions(args);

        trialResults = DataSearch.getResults(con, cmd);
        expectedResults = new ArrayList<>();
        expectedResults.add("ID3v23withSuffixInUPPERCASE.MP3");

        Assert.assertEquals(trialResults, expectedResults);
    }
}