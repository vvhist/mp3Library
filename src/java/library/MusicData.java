package library;

import java.io.File;
import java.io.FileFilter;

public class MusicData {

    private static void addMp3FromFolder(Database database, File folder) {
        FileFilter mp3Files = pathname -> pathname.isDirectory()
                || pathname.getName().toLowerCase().endsWith(".mp3");
        File[] folderContents = folder.listFiles(mp3Files);
        for (File file : folderContents != null ? folderContents : new File[0]) {
            if (file.isDirectory()) {
                addMp3FromFolder(database, file);
            } else {
                database.add(file);
            }
        }
    }

    public static Database create(File folder) {
        Database database = new Database();
        addMp3FromFolder(database, folder);
        return database;
    }
}