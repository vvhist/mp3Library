package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class SwingListeners {

    private SwingView view;
    private String column = "title";

    public SwingListeners(SwingView swingView) {
        this.view = swingView;
        enableView(false, "Select your music folder");
        view.getSelectButton().setEnabled(true);

        view.getSelectButton().addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = chooser.showDialog(null, "Select");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                MusicData.setMusicFolder(chooser.getSelectedFile());
                view.getPathLabel().setVisible(true);
                view.getPathLabel().setText(MusicData.getMusicFolder().getPath());
                if (!MusicData.getDatabaseLocation().exists()) {
                    enableView(false, "Creating a database in");
                    switchToWaitingMode(true);
                    new DatabaseCreator().execute();
                } else {
                    enableView(true, "Search in");
                }
            }
        });

        view.getUpdateButton().addActionListener(e -> {
            enableView(false, "Updating the database in");
            switchToWaitingMode(true);
            new DatabaseUpdater().execute();
        });

        view.getSearchButton().addActionListener(e -> {
            if (!Objects.equals(view.getOutputTextArea().getText(), "")) {
                view.getOutputTextArea().setText("");
            }
            try {
                Application.setConnection();
                ArrayList<String> searchPairs = getSearchPairs();
                if (searchPairs.size() >= 2) {
                    DataSearch.setQuery(searchPairs);
                    ResultSet results = DataSearch.getResults(column);
                    if (Objects.equals(column, "all")) {
                        view.setTable(createTable(results));
                        view.getScrollPane().setViewportView(view.getTable());
                    } else {
                        while (results.next()) {
                            view.getOutputTextArea().append(results.getString(1) + "\n");
                        }
                        view.getScrollPane().setViewportView(view.getOutputTextArea());
                    }
                }
            } catch (SQLException ex) {
                view.getPathLabel().setVisible(false);
                view.getMsgLabel().setText("SQL error");
            }
        });

        view.getTitlesRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(false);
            column = "title";
        });

        view.getFileNamesRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(true);
            column = "fileName";
        });

        view.getDisplayAllRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(true);
            column = "all";
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

    private class DatabaseCreator extends SwingWorker {
        @Override
        protected Object doInBackground() throws Exception {
            Application.setConnection();
            MusicData.create();
            return null;
        }

        @Override
        protected void done() {
            try {
                if (!MusicData.hasMp3Files()) {
                    MusicData.delete();
                    view.getSelectButton().setEnabled(true);
                    view.getMsgLabel().setText("No MP3 files were found in");
                } else {
                    enableView(true, "A new database was created in");
                }
                get();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof SQLException) {
                    view.getPathLabel().setVisible(false);
                    view.getMsgLabel().setText("SQL error");
                }
            }
            switchToWaitingMode(false);
        }
    }

    private class DatabaseUpdater extends SwingWorker {
        @Override
        protected Object doInBackground() throws Exception {
            Application.setConnection();
            MusicData.rebuild();
            return null;
        }

        @Override
        protected void done() {
            try {
                enableView(true, "The database was updated in");
                get();
            } catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof SQLException) {
                    view.getPathLabel().setVisible(false);
                    view.getMsgLabel().setText("SQL error");
                }
            }
            switchToWaitingMode(false);
        }
    }

    private void enableView(boolean isEnabled, String message) {
        enableComponents(view.$$$getRootComponent$$$(), isEnabled);
        if (isEnabled) {
            view.getTitleTextField().setEnabled(false);
        } else {
            view.getPathLabel().setEnabled(true);
            view.getMsgLabel().setEnabled(true);
        }
        view.getMsgLabel().setText(message);
    }

    private void switchToWaitingMode(boolean isInWaitingMode) {
        if (isInWaitingMode) {
            view.$$$getRootComponent$$$().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            view.$$$getRootComponent$$$().setCursor(null);
        }
        view.getProgressBar().setEnabled(isInWaitingMode);
        view.getProgressBar().setVisible(isInWaitingMode);
    }

    private static void enableComponents(Container container, boolean isEnabled) {
        for (Component component : container.getComponents()) {
            component.setEnabled(isEnabled);
            if (component instanceof Container) {
                enableComponents((Container) component, isEnabled);
            }
        }
    }

    private ArrayList<String> getSearchPairs() {
        ArrayList<String> searchValues = new ArrayList<>();
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

    private JTable createTable(ResultSet results) throws SQLException {
        int numberOfColumns = results.getMetaData().getColumnCount();
        Vector<Vector<String>> rowData = new Vector<>();
        while (results.next()) {
            Vector<String> row = new Vector<>(numberOfColumns);
            for (int i = 1; i <= numberOfColumns; i++) {
                row.add(results.getString(i));
            }
            rowData.add(row);
        }
        Vector<String> columnNames = new Vector<>(numberOfColumns);
        for (int i = 1; i <= numberOfColumns; i++) {
            columnNames.add(results.getMetaData().getColumnName(i));
        }
        JTable table = new JTable(rowData, columnNames);
        table.setColumnSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);
        return table;
    }
}