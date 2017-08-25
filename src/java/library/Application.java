package library;

import org.apache.commons.cli.*;

import java.io.File;
import java.sql.*;

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
        Option rebuild = Option.builder().longOpt("rebuild").hasArg(false).build();
        Option getPath = Option.builder().longOpt("path").hasArg(false).build();
        options.addOption(search);
        options.addOption(rebuild);
        options.addOption(getPath);
        return parser.parse(options, args);
    }

    private static boolean hasFolderMp3Files(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM mp3Lib");
        return resultSet.isBeforeFirst();
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        for(File file: files != null ? files : new File[0]) {
            if(file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        folder.delete();
    }

    private static void runProgram(String[] args) throws ParseException, SQLException,
            Mp3FilesNotFoundException {

        CommandLine cmd = createCommandLineOptions(args);

        File musicFolder = new File(args[0]);
        if (!musicFolder.isDirectory()) throw new IllegalArgumentException();
        File dbLocation = new File(musicFolder, "libraryData/Data");
        String url = "jdbc:hsqldb:file:" + dbLocation;

        if (cmd.hasOption("rebuild") && dbLocation.getParentFile().exists()) {
            Connection con = DriverManager.getConnection(url, "user", "");
            MusicData.rebuild(con, musicFolder);
            System.out.println("Data was updated in " + dbLocation);
        }
        if (!dbLocation.getParentFile().exists()) {
            Connection con = DriverManager.getConnection(url, "user", "");
            MusicData.create(con, musicFolder);
            if (!hasFolderMp3Files(con)) {
                Statement stmt = con.createStatement();
                stmt.execute("SHUTDOWN");
                deleteFolder(dbLocation.getParentFile());
                throw new Mp3FilesNotFoundException();
            }
            System.out.println("Data was saved in " + dbLocation);
        }
        if (cmd.hasOption("search")) {                  // Add your search keys in library.MusicData
            Connection con = DriverManager.getConnection(url, "user", "");
            for (String result : DataSearch.getResults(con, cmd)) {
                System.out.println(result);
            }
        }
    }

    public static void main(String[] args) {
        try {
            runProgram(args);
        } catch (SQLException e) {
            System.err.println("SQL error");
        } catch (ParseException e) {
            System.err.println("Parse error");
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            System.out.println("Please first enter the path of your MusicFolder!");
        } catch (Mp3FilesNotFoundException e) {
            System.out.println("No .mp3 files were found!");
        }
    }
}