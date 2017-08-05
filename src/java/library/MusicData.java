package library;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicData {

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

    private static void addToDatabase(Database database, File file) {
        Mp3File track;
        try {
            track = new Mp3File(file);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            System.err.println("Error: the program failed to process " + file);
            return;
        }
        ID3Wrapper tag = new ID3Wrapper(track.getId3v1Tag(), track.getId3v2Tag());
        if (database.data.isEmpty()) {
            database.data.put("FileName", new ArrayList<>());
            database.data.put("Artist",   new ArrayList<>());
            database.data.put("Title",    new ArrayList<>());
            database.data.put("Album",    new ArrayList<>());
            database.data.put("Genre",    new ArrayList<>());
            database.data.put("Year",     new ArrayList<>());
        }
        database.data.get("FileName").add(file.getName());
        database.data.get("Artist")  .add(tag.getArtist());
        database.data.get("Title")   .add(tag.getTitle());
        database.data.get("Album")   .add(tag.getAlbum());
        database.data.get("Genre")   .add(getGenre(track));
        database.data.get("Year")    .add(tag.getYear());
    }

    private static void addMp3FromFolder(Database database, File folder) {
        FileFilter mp3Files = pathname -> pathname.isDirectory()
                || pathname.getName().toLowerCase().endsWith(".mp3");
        File[] folderContents = folder.listFiles(mp3Files);
        for (File file : folderContents != null ? folderContents : new File[0]) {
            if (file.isDirectory()) {
                addMp3FromFolder(database, file);
            } else {
                addToDatabase(database, file);
            }
        }
    }

    public static Database create(File folder) {
        Database database = new Database();
        database.data = new HashMap<>(8);
        addMp3FromFolder(database, folder);
        return database;
    }
}