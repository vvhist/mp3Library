package library;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.*;

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
        pstmt.setString(1, entry.getFileName());
        pstmt.setString(2, entry.getArtist());
        pstmt.setString(3, entry.getTitle());
        pstmt.setString(4, entry.getAlbum());
        pstmt.setString(5, entry.getGenre());
        pstmt.setString(6, entry.getYear());
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
        stmt.executeUpdate("CREATE TABLE mp3Lib (" +
                "fileName VARCHAR(10000) NOT NULL," +
                "artist VARCHAR(10000)," +
                "title VARCHAR(10000)," +
                "album VARCHAR(10000)," +
                "genre VARCHAR(10000)," +
                "year VARCHAR(10000)," +
                "PRIMARY KEY (fileName))");
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO mp3Lib" +
                "(fileName, artist, title, album, genre, year)" +
                "VALUES (?, ?, ?, ?, ?, ?)");
        addMp3FromFolder(pstmt, musicFolder);
    }
}