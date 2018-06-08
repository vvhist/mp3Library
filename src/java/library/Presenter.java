package library;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public final class Presenter {

    private View view;
    private LibraryData library;
    private String url;

    public Presenter(View view) {
        this.view = view;
        view.enterSelectionMode("Select your music folder");
    }

    public void select() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = chooser.showDialog(null, "Select");
        if (returnValue != JFileChooser.APPROVE_OPTION) return;

        File musicFolder = chooser.getSelectedFile();
        view.displayPath(musicFolder.getPath());
        File DatabaseLocation = new File(musicFolder, "libraryData");

        library = new LibraryData(DatabaseLocation);
        url = "jdbc:hsqldb:file:" + new File(DatabaseLocation, "Data");

        if (DatabaseLocation.exists()) {
            view.enterSearchingMode("Search in");
        } else {
            view.enterWaitingMode("Creating a database in");
            new DatabaseCreator("A new database was created in").execute();
        }
    }

    public void update() {
        try (Connection con = DriverManager.getConnection(url, "user", "");
             Statement stmt = con.createStatement()) {
            Log.get().info("Connection is established to " + url);

            library.delete(stmt);
        } catch (SQLException ex) {
            view.enterExceptionMode("SQL error");
            Log.get().log(Level.SEVERE, "While updating", ex);
            ex.printStackTrace();
        }
        view.enterWaitingMode("Updating the database in");
        new DatabaseCreator("The database was updated in").execute();
    }

    public void search(Map<String, String> searchValues) {
        searchValues.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        if (searchValues.size() < 1) return;

        try (Connection con = DriverManager.getConnection(url, "user", "")) {
            Log.get().info("Connection is established to " + url);

            DataSearch search = new DataSearch(con, searchValues);
            view.updateTable(search.getTableModel());
        } catch (SQLException ex) {
            view.enterExceptionMode("SQL error");
            Log.get().log(Level.SEVERE, "While searching", ex);
            ex.printStackTrace();
        }
    }

    public void displayTitles() {
        DataSearch.setFilter(DataSearch.ColumnFilter.TITLE);
    }

    public void displayFileNames() {
        DataSearch.setFilter(DataSearch.ColumnFilter.FILENAME);
    }

    public void displayAll() {
        DataSearch.setFilter(DataSearch.ColumnFilter.ALL);
    }


    private class DatabaseCreator extends SwingWorker<Boolean, Void> {

        String message;

        DatabaseCreator(String message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground() throws SQLException {
            try (Connection con = DriverManager.getConnection(url, "user", "");
                 Statement stmt = con.createStatement()) {
                Log.get().info("Connection is established to " + url);

                library.create(con);
                if (library.hasMp3Files(stmt)) {
                    return true;
                } else {
                    library.delete(stmt);
                    return false;
                }
            }
        }

        @Override
        protected void done() {
            try {
                Boolean hasMp3Files = get();
                if (hasMp3Files) {
                    view.enterSearchingMode(message);
                } else {
                    view.enterSelectionMode("No MP3 files were found in");
                }
            } catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof SQLException) {
                    view.enterExceptionMode("SQL error");
                }
                Log.get().log(Level.SEVERE, "While creating a database", e);
                e.printStackTrace();
            }
        }
    }
}