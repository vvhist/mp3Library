package library;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class LibraryData {

    private static File musicFolder;

    public static void setMusicFolder(File folder) {
        musicFolder = folder;
    }

    public static File getMusicFolder() {
        return musicFolder;
    }

    public static File getData() {
        return new File(musicFolder, "libraryData/Data");
    }

    public static File getDataLocation() {
        return getData().getParentFile();
    }

    private static String getTags() {
        String[] tags = DataEntry.getTagNames();
        Arrays.setAll(tags, i -> tags[i] + " VARCHAR(10000)");
        tags[0] = tags[0] + " NOT NULL";
        return String.join(", ", tags);
    }

    private static void addMp3FromFolder(PreparedStatement pstmt, File folder) throws SQLException {
        FileFilter mp3Files = pathname -> pathname.isDirectory()
                || pathname.getName().toLowerCase().endsWith(".mp3");
        File[] folderContents = folder.listFiles(mp3Files);
        for (File file : folderContents != null ? folderContents : new File[0]) {
            if (file.isDirectory()) {
                addMp3FromFolder(pstmt, file);
            } else {
                try {
                    addToDatabase(pstmt, new DataEntry(file));
                } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                    System.err.println("Error: the program failed to process " + file.getPath());
                }
            }
        }
    }

    private static void addToDatabase(PreparedStatement pstmt, DataEntry entry) throws SQLException {
        for (int i = 0; i < entry.getSize(); i++) {
            pstmt.setString(i + 1, entry.getTags()[i]);
        }
        pstmt.executeUpdate();
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

    public static boolean hasMp3Files() throws SQLException {
        Statement stmt = Application.getConnection().createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM mp3Lib");
        return resultSet.isBeforeFirst();
    }

    public static void delete() throws SQLException {
        Statement stmt = Application.getConnection().createStatement();
        stmt.execute("SHUTDOWN");
        deleteFolder(getDataLocation());
    }

    public static void rebuild() throws SQLException {
        Statement stmt = Application.getConnection().createStatement();
        stmt.executeUpdate("DROP TABLE mp3Lib");
        create();
    }

    public static void create() throws SQLException {
        Connection con = Application.getConnection();
        Statement stmt = con.createStatement();
        stmt.execute("SET IGNORECASE TRUE");
        stmt.executeUpdate("CREATE TABLE mp3Lib (" + getTags() + ", PRIMARY KEY (fileName))");
        PreparedStatement pstmt = con.prepareStatement(
                "INSERT INTO mp3Lib VALUES (?, ?, ?, ?, ?, ?)");
        addMp3FromFolder(pstmt, musicFolder);
    }
}