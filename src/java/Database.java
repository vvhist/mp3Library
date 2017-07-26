import java.io.*;
import java.util.ArrayList;

class Database implements Serializable {
    ArrayList<File> data;
    static void serialize(String name, ArrayList<File> data) {
        try {
            Database database = new Database();
            database.data = data;
            FileOutputStream fileOut = new FileOutputStream(name);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(database);
            out.close();
            fileOut.close();
            System.out.println("Data is saved in " + name);
        } catch (IOException e) {
            System.err.println("Error: the program failed to save your data in " + name);
            System.exit(1);
        }
    }
    static Database deserialize(String name) {
        try {
            FileInputStream fileIn = new FileInputStream(name);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Database database = (Database) in.readObject();
            in.close();
            fileIn.close();
            return database;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: the program failed to get your data from " + name);
            System.exit(1);
        }
        return null;
    }
}