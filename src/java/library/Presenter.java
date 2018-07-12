package library;

import javax.swing.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public final class Presenter {

    private View view;
    private File musicFolder;
    private Database database;

    public Presenter(View view) {
        this.view = view;
        view.enterSelectionMode("Select your music folder");
    }

    public void select() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = chooser.showDialog(null, "Select");
        if (returnValue != JFileChooser.APPROVE_OPTION) return;

        musicFolder = chooser.getSelectedFile();
        view.displayPath(musicFolder.getPath());
        Log.get().info("Selected music folder: " + musicFolder.getPath());

        File location = new File(musicFolder, "libraryData");
        database = new Database(location);

        if (location.exists()) {
            view.enterSearchingMode("Search in");
        } else {
            view.enterWaitingMode("Creating a database in");
            new DatabaseCreator("A new database was created in").execute();
        }
    }

    public void update() {
        try {
            database.clear();
        } catch (SQLException e) {
            view.enterExceptionMode("SQL error");
            Log.get().log(Level.SEVERE, "While updating", e);
            e.printStackTrace();
        }
        view.enterWaitingMode("Updating the database in");
        new DatabaseCreator("The database was updated in").execute();
    }

    public void search(Map<String, String> searchValues) {
        searchValues.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        if (searchValues.isEmpty()) return;

        try {
            List<DataEntry> results = database.search(searchValues);
            String[][] tableData = SearchProcessing.createTableData(results);

            view.updateTable(tableData, DataEntry.getTagNames());
        } catch (SQLException ex) {
            view.enterExceptionMode("SQL error");
            Log.get().log(Level.SEVERE, "While searching", ex);
            ex.printStackTrace();
        }
    }


    private class DatabaseCreator extends SwingWorker<Boolean, Void> {

        String message;

        DatabaseCreator(String message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground() throws SQLException {
            AudioData data = new AudioData(musicFolder);
            if (data.isAvailable()) {
                List<DataEntry> entries = data.extract();
                database.create(entries);
                return true;
            } else {
                database.delete();
                return false;
            }
        }

        @Override
        protected void done() {
            try {
                Boolean hasMp3Files = get();
                if (hasMp3Files) {
                    view.enterSearchingMode(message);
                    Log.get().info(message + " " + musicFolder.getPath());
                } else {
                    String msg = "No MP3 files were found in";
                    view.enterSelectionMode(msg);
                    Log.get().info(msg + " " + musicFolder.getPath());
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