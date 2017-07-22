import java.io.*;
import java.util.ArrayList;

class Database implements Serializable {
    ArrayList<File> data;
    static void serialize(String name, ArrayList<File> data) throws IOException {
        Database database = new Database();
        database.data = data;
        FileOutputStream fileOut = new FileOutputStream(name);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(database);
        out.close();
        fileOut.close();
        System.out.println("Data is saved in " + name);
    }
    static Database deserialize(String name) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(name);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Database database = (Database) in.readObject();
        in.close();
        fileIn.close();
        return database;
    }
}