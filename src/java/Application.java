import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Application {
    private static CommandLine createCommandLineOptions(String[] args) {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        Option search  = Option.builder().longOpt("search").valueSeparator().hasArgs().build();
        Option rebuild = Option.builder().longOpt("rebuild").hasArg(false).build();
        options.addOption(search);
        options.addOption(rebuild);
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }
    }
    static boolean firstArgumentIsFolder(String[] args) {
        return args.length != 0 && new File(args[0]).isDirectory();
    }
    public static void main(String[] args) {
        CommandLine cmd = createCommandLineOptions(args);
        if (cmd == null) {
            System.err.println("Parse error");
            return;
        }
        if (!firstArgumentIsFolder(args)) {
            System.out.println("Please enter the path of your MusicFolder!");
            return;
        }
        File musicFolder = new File(args[0]);
        String databasePath = musicFolder + File.separator + "database.ser";

        if (!new File(databasePath).exists() || cmd.hasOption("rebuild")) {
            ArrayList<File> data = MusicData.create(musicFolder);
            if (data == null) {
                System.out.println("No .mp3 files were found!");
                return;
            }
            try {
                Database.serialize(databasePath, data);
                System.out.println("Data is saved in " + databasePath);
            } catch (IOException e) {
                System.err.println("Error: the program failed to save the data in " + databasePath);
                return;
            }
        }
        if (cmd.hasOption("search")) {      // Add your search keys in DataSearch.createTagsMap()
            Database database = Database.deserialize(databasePath);
            if (database == null) {
                System.err.println("Error: the program failed to get the data from " + databasePath);
                return;
            }
            for (String name : DataSearch.getResults(cmd.getOptionValues("search"), database.data)) {
                System.out.println(name);
            }
        }
    }
}