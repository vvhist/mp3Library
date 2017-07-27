import java.io.File;
import java.util.ArrayList;

class MusicData {
    static ArrayList<File> create(File folder) {
        ArrayList<File> data = new ArrayList<>();
        addMp3FilesFromFolder(data, folder);
        if (data.size() == 0) {
            return null;
        }
        return data;
    }
    private static void addMp3FilesFromFolder(ArrayList<File> data, File folder) {
        File[] folderContents = folder.listFiles(pathname -> pathname.isDirectory()
                || pathname.getName().toLowerCase().endsWith(".mp3"));
        for (File file : folderContents != null ? folderContents : new File[0]) {
            if (file.isDirectory()) {
                addMp3FilesFromFolder(data, file);
            } else data.add(file);
        }
    }
}