package library;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.*;

public class MusicData {

    private static File musicFolder;

    public static void setMusicFolder(File musicFolder) {
        MusicData.musicFolder = musicFolder;
    }

    public static File getMusicFolder() {
        return musicFolder;
    }

    public static File getDatabase() {
        return new File(musicFolder, "libraryData/Data");
    }

    public static File getDatabaseLocation() {
        return getDatabase().getParentFile();
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
                    addToDatabase(pstmt, file);
                } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                    System.err.println("Error: the program failed to process " + file.getPath());
                }
            }
        }
    }

    private static void addToDatabase(PreparedStatement pstmt, File file) throws SQLException,
            IOException, UnsupportedTagException, InvalidDataException {

        Mp3File track  = new Mp3File(file);
        ID3Wrapper tag = new ID3Wrapper(track.getId3v1Tag(), track.getId3v2Tag());
        pstmt.setString(1, file.getName());
        pstmt.setString(2, tag.getArtist());
        pstmt.setString(3, tag.getTitle());
        pstmt.setString(4, tag.getAlbum());
        pstmt.setString(5, getGenre(track));
        pstmt.setString(6, tag.getYear());
        pstmt.executeUpdate();
    }

    /**
     * This method does exactly the same thing as getAlbum() or getTitle() methods of
     * com.mpatric.mp3agic.ID3Wrapper class.
     *
     * For some reason ID3v1 tag was given higher priority than ID3v2 tag
     * solely in getGenre() and getGenreDescription() methods of the above-mentioned class.
     * Since Windows, on the contrary, gives higher priority to ID3v2 tags,
     * these methods can cause unexpected output data when both tags are present in the file,
     * so I've decided not to use them.
     *
     * @param track Investigated .mp3 file.
     * @return Genre name (or null if it's absent) to put in database.
     */
    private static String getGenre(Mp3File track) {
        if (track.hasId3v2Tag() && track.getId3v2Tag().getGenreDescription() != null
                && track.getId3v2Tag().getGenreDescription().length() > 0) {
            return track.getId3v2Tag().getGenreDescription();
        } else if (track.hasId3v1Tag()) {
            return track.getId3v1Tag().getGenreDescription();
        } else {
            return null;
        }
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
        deleteFolder(getDatabaseLocation());
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