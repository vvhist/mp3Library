package library;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database implements Serializable {

    public HashMap<String, ArrayList<String>> data;

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