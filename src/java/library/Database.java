package library;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.*;
import java.util.ArrayList;

public class Database implements Serializable {

    private static final long serialVersionUID = 1657573116580191624L;
    private ArrayList<TagEntry> data;

    public Database() {
        data = new ArrayList<>();
    }

    public ArrayList<TagEntry> getData() {
        return data;
    }

    public void add(File file) {
        TagEntry entry = new TagEntry();
        try {
            entry.setTag(file);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            System.err.println("Error: the program failed to process " + file);
            return;
        }
        data.add(entry);
    }

    public static void serialize(Database database, File location) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(location);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(database);
        out.close();
        fileOut.close();
    }

    public static Database deserialize(File location) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(location);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Database database = (Database) in.readObject();
        in.close();
        fileIn.close();
        return database;
    }
}