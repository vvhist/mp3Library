import org.apache.commons.cli.*;

import java.io.File;
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
            System.err.println("Parse error");
            System.exit(1);
        }
        return null;
    }
    static boolean firstArgumentIsFolder(String[] args) {
        if (args.length == 0 || !new File(args[0]).isDirectory()) {
            System.out.println("Please enter the path of your MusicFolder!");
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        CommandLine cmd = createCommandLineOptions(args);
        if (cmd == null || !firstArgumentIsFolder(args)) return;
        File musicFolder = new File(args[0]);
        String databasePath = musicFolder + File.separator + "database.ser";

        if (!new File(databasePath).exists() || cmd.hasOption("rebuild")) {
            ArrayList<File> data = MusicData.create(musicFolder);
            if (data == null) return;
            Database.serialize(databasePath, data);
        }
        if (cmd.hasOption("search")) {      // Add your search keys in DataSearch.createTagsMap()
            Database database = Database.deserialize(databasePath);
            for (String name : DataSearch.getResults(cmd.getOptionValues("search"), database.data)) {
                System.out.println(name);
            }
        }
    }
}