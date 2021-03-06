package library;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;

public final class DataEntry {

    private final String filename;
    private final String artist;
    private final String title;
    private final String album;
    private final String genre;
    private final Integer year;

    public DataEntry(String filename, String artist, String title, String album, String genre,
                     Integer year) {
        this.filename = filename;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.genre = genre;
        this.year = year;
    }

    public DataEntry(File file) throws IOException, UnsupportedTagException, InvalidDataException {
        Mp3File mp3 = new Mp3File(file);
        ID3Wrapper tag = new ID3Wrapper(mp3.getId3v1Tag(), mp3.getId3v2Tag()) {
            /**
             * This method swaps two return statements of the overridden one.
             * <p>
             * For some reason ID3v1 tag was given higher priority than ID3v2 tag solely in
             * {@link com.mpatric.mp3agic.ID3Wrapper#getGenreDescription()} and {@link #getGenre()}.
             * Since Windows, on the contrary, gives higher priority to ID3v2 tags,
             * these methods can cause unexpected output data when both tags are present
             * in the file, so I've decided not to use them.
             *
             * @return Genre name (or null if it's absent) to put in database.
             */
            @Override
            public String getGenreDescription() {
                if (getId3v2Tag() != null) {
                    return getId3v2Tag().getGenreDescription();
                } else if (getId3v1Tag() != null) {
                    return getId3v1Tag().getGenreDescription();
                } else {
                    return null;
                }
            }
        };
        filename = file.getName();
        artist = tag.getArtist();
        title  = tag.getTitle();
        album  = tag.getAlbum();
        genre  = tag.getGenreDescription();

        String year = tag.getYear();
        this.year = ((year == null) || year.equals(""))
                ? null
                : Integer.valueOf(tag.getYear());
    }

    public String getFilename() {
        return filename;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getYear() {
        return year;
    }

    public static String[] getTagNames() {
        return new String[] {"Filename", "Artist", "Title", "Album", "Genre", "Year"};
    }

    public static int getSize() {
        return getTagNames().length;
    }
}