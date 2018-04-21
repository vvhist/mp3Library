package library;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;

public class DataEntry {

    private static int id = 0;
    private String fileName;
    private String artist;
    private String title;
    private String album;
    private String genre;
    private String year;

    public DataEntry() {}

    public DataEntry(File file) throws IOException, UnsupportedTagException, InvalidDataException {
        Mp3File mp3 = new Mp3File(file);
        ID3Wrapper tag = new ID3Wrapper(mp3.getId3v1Tag(), mp3.getId3v2Tag());
        id++;
        fileName = file.getName();
        artist = tag.getArtist();
        title  = tag.getTitle();
        album  = tag.getAlbum();
        genre  = getGenre(mp3);
        year   = tag.getYear();
    }

    public static void setIDToZero() {
        id = 0;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getTags() {
        return new String[] {Integer.toString(id), fileName, artist, title, album, genre, year};
    }

    public void setTags(String[] tags) {
        fileName = tags[0];
        artist   = tags[1];
        title    = tags[2];
        album    = tags[3];
        genre    = tags[4];
        year     = tags[5];
    }

    public int getSize() {
        return getTags().length;
    }

    public static String[] getTagNames() {
        return new String[] {"Id", "Filename", "Artist", "Title", "Album", "Genre", "Year"};
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
     * @param mp3 Investigated .mp3 file.
     * @return Genre name (or null if it's absent) to put in database.
     */
    private static String getGenre(Mp3File mp3) {
        if (mp3.hasId3v2Tag() && mp3.getId3v2Tag().getGenreDescription() != null
                && mp3.getId3v2Tag().getGenreDescription().length() > 0) {
            return mp3.getId3v2Tag().getGenreDescription();
        } else if (mp3.hasId3v1Tag()) {
            return mp3.getId3v1Tag().getGenreDescription();
        } else {
            return null;
        }
    }
}