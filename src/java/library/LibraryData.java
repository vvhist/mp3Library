package library;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class LibraryData {

    private SQLConnection con;
    private Statement stmt;
    private PreparedStatement pstmt;

    public LibraryData(SQLConnection con) throws SQLException {
        this.con = con;
        con.establish();
        stmt = con.get().createStatement();
    }

    public void create() throws SQLException {
        stmt.execute("SET IGNORECASE TRUE");
        stmt.executeUpdate(
                "CREATE TABLE mp3Lib (" + getTagsWithSQLSyntax() + ", PRIMARY KEY (id))");
        pstmt = con.get().prepareStatement(
                "INSERT INTO mp3Lib VALUES (?, ?, ?, ?, ?, ?, ?)");
        addMp3FromFolder(con.getDataLocation().getParentFile());
        DataEntry.setIDToZero();
    }

    public boolean hasMp3Files() throws SQLException {
        ResultSet resultSet = stmt.executeQuery("SELECT * FROM mp3Lib");
        return resultSet.isBeforeFirst();
    }

    public void delete() throws SQLException {
        stmt.execute("SHUTDOWN");
        deleteFolder(con.getDataLocation());
    }

    private static String getTagsWithSQLSyntax() {
        String[] tags = DataEntry.getTagNames();
        Arrays.setAll(tags, i -> tags[i] + " VARCHAR(10000)");
        return String.join(", ", tags);
    }

    private void addMp3FromFolder(File folder) throws SQLException {
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

    private void addToDatabase(DataEntry entry) throws SQLException {
        for (int i = 0; i < DataEntry.getSize(); i++) {
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