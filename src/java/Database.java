import java.io.*;
import java.util.ArrayList;

class Database implements Serializable {

    // Поля отделяются пустой строкой.
    // Модификаторы доступа.
    // Использовать File в качестве элемента хранения на самом деле не лучшая идея.
    // Ведь можно использовать готовые теги чтобы лишний раз их не читать.
    ArrayList<File> data;

    // Нет модификатора доступа (public),
    static void serialize(String name, ArrayList<File> data) throws IOException {
        // не проще ли передать Database который был сконструирован ранее при deserialize,
        // либо через Music data создать сразу Database.
        Database database = new Database();
        database.data = data;
        FileOutputStream fileOut = new FileOutputStream(name);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(database);
        out.close();
        fileOut.close();
    }

    // Пустая строка между методами и public
    static Database deserialize(String name) {
        try {
            FileInputStream fileIn = new FileInputStream(name);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Database database = (Database) in.readObject();
            in.close();
            fileIn.close();
            return database;
        } catch (IOException | ClassNotFoundException e) {
            return null; // лучше пробросить исключение выше
        }
    }
}