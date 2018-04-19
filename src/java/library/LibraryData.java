package library;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class LibraryData {

    private static Connection con;
    private static Statement stmt;
    private static PreparedStatement pstmt;
    private File musicFolder;

    public LibraryData(File folder) {
        musicFolder = folder;
    }

    public static Statement getSQLStatement() {
        return stmt;
    }

    public File getMusicFolder() {
        return musicFolder;
    }

    public File getDataLocation() {
        return new File(musicFolder, "libraryData");
    }

    public void create() throws SQLException {
        establishConnection();
        stmt.execute("SET IGNORECASE TRUE");
        stmt.executeUpdate(
                "CREATE TABLE mp3Lib (" + getTagsWithSQLSyntax() + ", PRIMARY KEY (fileName))");
        pstmt = con.prepareStatement("INSERT INTO mp3Lib VALUES (?, ?, ?, ?, ?, ?)");
        addMp3FromFolder(musicFolder);
    }

    public void rebuild() throws SQLException {
        establishConnection();
        stmt.executeUpdate("DROP TABLE mp3Lib");
        create();
    }

    public boolean hasMp3Files() throws SQLException {
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM mp3Lib");
        return resultSet.isBeforeFirst();
    }

    public void delete() throws SQLException {
        stmt.execute("SHUTDOWN");
        deleteFolder(getDataLocation());
    }

    private void establishConnection() throws SQLException {
        con = DriverManager.getConnection(
                "jdbc:hsqldb:file:" + new File(getDataLocation(), "Data"), "user", "");
        stmt = con.createStatement();
    }

    private static String getTagsWithSQLSyntax() {
        String[] tags = DataEntry.getTagNames();
        Arrays.setAll(tags, i -> tags[i] + " VARCHAR(10000)");
        tags[0] = tags[0] + " NOT NULL";
        return String.join(", ", tags);
    }

    private static void addMp3FromFolder(File folder) throws SQLException {
        FileFilter mp3Files = pathname -> pathname.isDirectory()
                || pathname.getName().toLowerCase().endsWith(".mp3");
        File[] folderContents = folder.listFiles(mp3Files);
        for (File file : folderContents != null ? folderContents : new File[0]) {
            if (file.isDirectory()) {
                addMp3FromFolder(file);
            } else {
                try {
                    addToDatabase(new DataEntry(file));
                } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                    System.err.println("Error: the program failed to process " + file.getPath());
                }
            }
        }
    }

    private static void addToDatabase(DataEntry entry) throws SQLException {
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
}