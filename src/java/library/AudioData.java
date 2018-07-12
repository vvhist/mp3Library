package library;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class AudioData {

    private List<File> mp3Files = new ArrayList<>();

    public AudioData(File musicFolder) {
        addMp3FromFolder(musicFolder, mp3Files);
    }

    public boolean isAvailable() {
        return !mp3Files.isEmpty();
    }

    public List<DataEntry> extract() {
        List<DataEntry> entries = new ArrayList<>();
        for (File mp3 : mp3Files) {
            try {
                entries.add(new DataEntry(mp3));
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                Log.get().log(Level.WARNING, "While processing " + mp3.getPath(), e);
                e.printStackTrace();
            }
        }
        return entries;
    }

    private void addMp3FromFolder(File folder, List<File> mp3Files) {
        FileFilter filter = pathname -> pathname.isDirectory()
                || pathname.getName().toLowerCase().endsWith(".mp3");
        File[] filteredFiles = folder.listFiles(filter);
        if (filteredFiles == null) return;

        for (File file : filteredFiles) {
            if (file.isDirectory()) {
                addMp3FromFolder(file, mp3Files);
            } else {
                mp3Files.add(file);
            }
        }
    }
}