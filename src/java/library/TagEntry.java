package library;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class TagEntry implements Serializable {

    private static final long serialVersionUID = 4836207102261836029L;
    private String fileName;
    private String artist;
    private String title;
    private String album;
    private String genre;
    private String year;

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (!(object instanceof TagEntry)) return false;

        TagEntry other = (TagEntry) object;
        return     Objects.equals(fileName, other.fileName)
                && Objects.equals(artist,   other.artist)
                && Objects.equals(title,    other.title)
                && Objects.equals(album,    other.album)
                && Objects.equals(genre,    other.genre)
                && Objects.equals(year,     other.year);
    }

    public String getTag(String key) {
        switch (key) {
            case "FileName": return fileName;
            case "Artist":   return artist;
            case "Title":    return title;
            case "Album":    return album;
            case "Genre":    return genre;
            case "Year":     return year;
            default: return "NoSuchKey";
        }
    }

    public void setTag(String key, String value) {
        switch (key) {
            case "FileName": fileName = value;
            case "Artist":   artist   = value;
            case "Title":    title    = value;
            case "Album":    album    = value;
            case "Genre":    genre    = value;
            case "Year":     year     = value;
        }
    }

    public void setTag(File file) throws IOException, UnsupportedTagException,
            InvalidDataException {

        Mp3File track  = new Mp3File(file);
        ID3Wrapper tag = new ID3Wrapper(track.getId3v1Tag(), track.getId3v2Tag());

        fileName = file.getName();
        artist   = tag.getArtist();
        title    = tag.getTitle();
        album    = tag.getAlbum();
        genre    = getGenre(track);
        year     = tag.getYear();
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
}