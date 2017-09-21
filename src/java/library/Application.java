package library;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {

    private static Connection con;

    public static void setConnection() throws SQLException {
        con = DriverManager.getConnection("jdbc:hsqldb:file:" + LibraryData.getData(), "user", "");
    }

    public static Connection getConnection() {
        return con;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new SwingListeners(new SwingView());
        });
    }
}