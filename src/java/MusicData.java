import java.io.File;
import java.util.ArrayList;

class MusicData {

    // ArrayList<File> неговорящее имя для передаваемых данных. Database - говорящее.
    static ArrayList<File> create(File folder) {
        ArrayList<File> data = new ArrayList<>();
        addMp3FilesFromFolder(data, folder);
        if (data.size() == 0) {
            return null; // never return null;
        }
        return data;
    }

    private static void addMp3FilesFromFolder(ArrayList<File> data, File folder) {
        // FileFilter mp3files = pathname -> pathname.isDirectory...
        File[] folderContents =
                folder.listFiles(pathname ->  // folder.listFiles(mp3files);
                        pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".mp3"));
        for (File file : folderContents != null ? folderContents : new File[0]) { // Сомневаюсь что оно может быть null
            if (file.isDirectory()) {
                addMp3FilesFromFolder(data, file);
            } else data.add(file); // Скобки обязательны
        }
    }
}