import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DatabaseUsage {
    private static CommandLine createCommandLineOptions(String[] args) throws ParseException{
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        Option search = Option.builder().longOpt("search").valueSeparator().hasArgs().build();
        Option rebuild = Option.builder().longOpt("rebuild").hasArg(false).build();
        options.addOption(search);
        options.addOption(rebuild);
        return parser.parse(options, args);
    }
    private static void checkExistenceOfArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter the path of your MusicFolder or your query!");
            System.exit(0);
        }
    }
    private static void checkExistenceOfDatabase(File folder, String name) throws IOException {
        File databaseFile = new File(folder + File.separator + name);
        if (!databaseFile.exists()) {
            if (!folder.isDirectory()) {
                System.out.println("Please first enter the path of your MusicFolder!");
                System.exit(0);
            }
            createDatabaseFile(folder, name);
        }
    }
    private static void createDatabaseFile(File myMusicFolder, String name) throws IOException {
        ArrayList<File> mp3List = new ArrayList<>();
        addMp3FilesFromFolder(mp3List, myMusicFolder);
        if (mp3List.size() == 0) {
            System.out.println("No .mp3 files were found!");
            System.exit(0);
        }
        serializeData(myMusicFolder + File.separator + name, mp3List);
        System.out.println("Data is saved in " + name);
    }
    private static void addMp3FilesFromFolder(ArrayList mp3Library, File folder) {
        if (folder.isDirectory()) {
            File[] folderContents = folder.listFiles(pathname -> pathname.isDirectory()
                    || pathname.getName().toLowerCase().endsWith(".mp3"));
            for (File file : folderContents) {
                if (file.getName().toLowerCase().endsWith(".mp3")) {
                    mp3Library.add(file);
                } else addMp3FilesFromFolder(mp3Library, file);
            }
        }
    }
    private static void serializeData(String name, ArrayList<File> list) throws IOException {
        Database data = new Database();
        data.libraryList = list;
        FileOutputStream fileOut = new FileOutputStream(name);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(data);
        out.close();
        fileOut.close();
    }
    private static Database deserializeData(String name) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(name);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Database data = (Database) in.readObject();
        in.close();
        fileIn.close();
        return data;
    }
    private static HashMap pairArgs(ArrayList<String> winTags, String[] optionValues) {
        HashMap pairedArgs = new HashMap(4);
        for (int i = 0; i < optionValues.length - 1; i = i + 2) {
            if (winTags.contains(optionValues[i])) {
                             pairedArgs.put(optionValues[i], optionValues[i + 1]);
            } else {
                System.out.println("Try this: D:\\Folder --search Year=2007 Genre=Jazz");
                System.exit(0);
            }
        }
        return pairedArgs;
    }
    private static void printOutput(Database data, HashMap pairedArgs, ArrayList<String> winTags) throws
            IOException, InvalidDataException, UnsupportedTagException {
        HashMap realValues = new HashMap(4);
        for (File file : data.libraryList) {
            Mp3File track = new Mp3File(file);
            ID3Wrapper tag = new ID3Wrapper(track.getId3v1Tag(), track.getId3v2Tag());
            realValues.put(winTags.get(0), tag.getArtist());
            realValues.put(winTags.get(1), tag.getYear());
            realValues.put(winTags.get(2), tag.getAlbum());
            realValues.put(winTags.get(3), tag.getGenreDescription());
            if (isSuitable(pairedArgs, winTags, realValues)) {
                System.out.println(tag.getTitle());
            }
        }
    }
    private static boolean isSuitable(HashMap pairedArgs, ArrayList<String> winTags,
                                      HashMap realValues) {
        for (String winTag : winTags) {
            if (pairedArgs.containsKey(winTag)
                    && !Objects.equals(pairedArgs.get(winTag), realValues.get(winTag))) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) throws IOException, ParseException, InvalidDataException,
            UnsupportedTagException, ClassNotFoundException {
        CommandLine cmd = createCommandLineOptions(args);
        checkExistenceOfArgs(args);
        File musicFolder = new File(args[0]);
        String databaseName = "database.ser";
        if (cmd.hasOption("rebuild")) {
            createDatabaseFile(musicFolder, databaseName);
        }
        checkExistenceOfDatabase(musicFolder, databaseName);
        if (cmd.hasOption("search")) {
            Database data = deserializeData(musicFolder + File.separator + databaseName);
            ArrayList<String> winTags = new ArrayList<>();
            winTags.add("Artist");
            winTags.add("Year");
            winTags.add("Album"); // You should also add tags in printOutput() to use them.
            winTags.add("Genre");
            HashMap pairedArgs = pairArgs(winTags, cmd.getOptionValues("search"));
            printOutput(data, pairedArgs, winTags);
        }
    }
}