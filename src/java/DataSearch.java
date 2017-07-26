import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

class DataSearch {
    private static HashMap createTagsMap(ID3Wrapper tag) {
        HashMap<String, String> tagsMap = new HashMap<>(4);
        tagsMap.put("Artist", tag.getArtist());
        tagsMap.put("Year",   tag.getYear());
        tagsMap.put("Album",  tag.getAlbum());
        tagsMap.put("Genre",  tag.getGenreDescription());
        return tagsMap;
    }
    private static boolean isSuitable(String[] optionValues, HashMap tagsMap) {
        for (int i = 0; i < optionValues.length - 1; i += 2) {
            if (tagsMap.containsKey(optionValues[i])
                    && !Objects.equals(tagsMap.get(optionValues[i]), optionValues[i + 1])) {
                return false;
            }
        }
        return true;
    }
    static ArrayList<String> getResults(String[] optionValues, ArrayList<File> data) {
        ArrayList<String> selectedData = new ArrayList<>();
        for (File file : data) {
            try {
                Mp3File track = new Mp3File(file);
                ID3Wrapper tag = new ID3Wrapper(track.getId3v1Tag(), track.getId3v2Tag());
                HashMap tagsMap = createTagsMap(tag);
                if (isSuitable(optionValues, tagsMap)) {
                    selectedData.add(tag.getTitle());
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                System.err.println("Error: the program failed to process " + file);
            }
        }
        return selectedData;
    }
}