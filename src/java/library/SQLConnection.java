package library;

import java.io.File;
import java.sql.*;

public class SQLConnection {

    private static Connection con;

    public static Connection get() {
        return con;
    }

    public static void establish(File DataLocation) throws SQLException {
        con = DriverManager.getConnection(
                "jdbc:hsqldb:file:" + new File(DataLocation, "Data"), "user", "");
    }
}