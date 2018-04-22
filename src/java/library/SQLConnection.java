package library;

import java.io.File;
import java.sql.*;

public class SQLConnection {

    private File DataLocation;
    private Connection con;

    public SQLConnection(File musicFolder) {
        DataLocation = new File(musicFolder, "libraryData");
    }

    public File getDataLocation() {
        return DataLocation;
    }

    public Connection get() {
        return con;
    }

    public void establish() throws SQLException {
        con = DriverManager.getConnection(
                "jdbc:hsqldb:file:" + new File(DataLocation, "Data"), "user", "");
    }
}