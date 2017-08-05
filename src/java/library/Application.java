package library;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

public class Application {

    private static class Mp3FilesNotFoundException extends Exception {
        public Mp3FilesNotFoundException() {}
        public Mp3FilesNotFoundException(String message) {
            super(message);
        }
        public Mp3FilesNotFoundException(Throwable cause) {
            super(cause);
        }
        public Mp3FilesNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static CommandLine createCommandLineOptions(String[] args) throws ParseException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        Option search  = Option.builder().longOpt("search").valueSeparator().hasArgs().build();
        Option rebuild = Option.builder().longOpt("rebuild")            .hasArg(false).build();
        Option getPath = Option.builder().longOpt("path")               .hasArg(false).build();
        options.addOption(search);
        options.addOption(rebuild);
        options.addOption(getPath);
        return parser.parse(options, args);
    }

    private static void runProgram(String[] args) throws IOException, ClassNotFoundException,
            ParseException, Mp3FilesNotFoundException {

        CommandLine cmd = createCommandLineOptions(args);
        File musicFolder = new File(args[0]);
        if (!musicFolder.isDirectory())      throw new IllegalArgumentException();
        File databaseLocation = new File(musicFolder, "database.ser");

        if (!databaseLocation.exists() || cmd.hasOption("rebuild")) {
            Database database = MusicData.create(musicFolder);
            if (database.data.isEmpty())     throw new Mp3FilesNotFoundException();
            Database.serialize(database, databaseLocation);
            System.out.println("Data was saved in " + databaseLocation);
        }
        if (cmd.hasOption("search")) {  // Add your search keys in library.MusicData.addToDatabase()
            Database database = Database.deserialize(databaseLocation);
            for (Object result : DataSearch.getResults(database, cmd)) {
                System.out.println(result);
            }
        }
    }

    public static void main(String[] args) {
        try {
            runProgram(args);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println("Please first enter the path of your MusicFolder!");
        } catch (ParseException e) {
            System.err.println("Parse error");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: the program failed to save or to get the data");
        } catch (Mp3FilesNotFoundException e) {
            System.out.println("No .mp3 files were found!");
        }
    }
}