package library;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class SwingListeners {

    private SwingView view = new SwingView();
    private SQLConnection con;
    private LibraryData library;

    public SwingListeners() {
        view.enable(false, "Select your music folder");
        view.getSelectButton().setEnabled(true);

        view.getSelectButton().addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = chooser.showDialog(null, "Select");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File musicFolder = chooser.getSelectedFile();
                con = new SQLConnection(musicFolder);
                view.getPathLabel().setVisible(true);
                view.getPathLabel().setText(musicFolder.getPath());
                if (!con.getDataLocation().exists()) {
                    view.enable(false, "Creating a database in");
                    view.switchToWaitingMode(true);
                    new DatabaseCreator("A new database was created in").execute();
                } else {
                    view.enable(true, "Search in");
                }
            }
        });

        view.getUpdateButton().addActionListener(e -> {
            try {
                library = new LibraryData(con);
                library.delete();
            } catch (SQLException ex) {
                view.getPathLabel().setVisible(false);
                view.getMsgLabel().setText("SQL error");
                Log.get().log(Level.SEVERE, "While updating", ex);
                ex.printStackTrace();
            }
            view.enable(false, "Updating the database in");
            view.switchToWaitingMode(true);
            new DatabaseCreator("The database was updated in").execute();
        });

        view.getSearchButton().addActionListener(e -> {
            try {
                List<String> searchValues = getSearchValues();
                if (searchValues.size() >= 2) {
                    DataSearch search = new DataSearch(con, searchValues);
                    view.getTable().setModel(search.getTableModel());
                }
            } catch (SQLException ex) {
                view.getPathLabel().setVisible(false);
                view.getMsgLabel().setText("SQL error");
                Log.get().log(Level.SEVERE, "While searching", ex);
                ex.printStackTrace();
            }
        });

        view.getTitlesRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(false);
            DataSearch.setFilter(DataSearch.ColumnFilter.TITLE);
        });

        view.getFileNamesRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(true);
            DataSearch.setFilter(DataSearch.ColumnFilter.FILENAME);
        });

        view.getDisplayAllRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(true);
            DataSearch.setFilter(DataSearch.ColumnFilter.ALL);
        });

        KeyAdapter searchOnEnter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    view.getSearchButton().doClick();
                }
            }
        };
        view.getArtistTextField().addKeyListener(searchOnEnter);
        view.getAlbumTextField().addKeyListener(searchOnEnter);
        view.getGenreTextField().addKeyListener(searchOnEnter);
        view.getYearTextField().addKeyListener(searchOnEnter);
        view.getTitleTextField().addKeyListener(searchOnEnter);
    }

    private class DatabaseCreator extends SwingWorker<Boolean, Void> {

        String message;

        DatabaseCreator(String message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground() throws SQLException {
            library = new LibraryData(con);
            library.create();
            if (library.hasMp3Files()) {
                return true;
            } else {
                library.delete();
                return false;
            }
        }

        @Override
        protected void done() {
            try {
                Boolean hasMp3Files = get();
                if (!hasMp3Files) {
                    view.getSelectButton().setEnabled(true);
                    view.getMsgLabel().setText("No MP3 files were found in");
                } else {
                    view.enable(true, message);
                }
                get();
            } catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof SQLException) {
                    view.getPathLabel().setVisible(false);
                    view.getMsgLabel().setText("SQL error");
                }
                Log.get().log(Level.SEVERE, "While creating a database", e);
                e.printStackTrace();
            }
            view.switchToWaitingMode(false);
        }
    }

    private List<String> getSearchValues() {
        List<String> searchValues = new ArrayList<>();
        if (!view.getArtistTextField().getText().isEmpty()) {
            searchValues.add("Artist");
            searchValues.add(view.getArtistTextField().getText());
        }
        if (!view.getAlbumTextField().getText().isEmpty()) {
            searchValues.add("Album");
            searchValues.add(view.getAlbumTextField().getText());
        }
        if (!view.getGenreTextField().getText().isEmpty()) {
            searchValues.add("Genre");
            searchValues.add(view.getGenreTextField().getText());
        }
        if (!view.getYearTextField().getText().isEmpty()) {
            searchValues.add("Year");
            searchValues.add(view.getYearTextField().getText());
        }
        if (view.getTitleTextField().isEnabled() && !view.getTitleTextField().getText().isEmpty()) {
            searchValues.add("Title");
            searchValues.add(view.getTitleTextField().getText());
        }
        return searchValues;
    }
}