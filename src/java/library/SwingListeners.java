package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class SwingListeners {

    private SwingView view;
    private File musicFolder;
    private File dbLocation;

    public SwingListeners(SwingView swingView) {
        this.view = swingView;
        enableView(false, "Select your music folder");
        view.getSelectButton().setEnabled(true);

        view.getSelectButton().addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = chooser.showDialog(null, "Select");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                musicFolder = chooser.getSelectedFile();
                dbLocation = new File(musicFolder, "libraryData/Data");
                view.getPathLabel().setVisible(true);
                view.getPathLabel().setText(musicFolder.getPath());
                if (!dbLocation.getParentFile().exists()) {
                    enableView(false, "Creating a database in");
                    switchToWaitingMode(true);
                    new Thread(databaseCreator).start();
                } else {
                    enableView(true, "Search in");
                }
            }
        });

        view.getUpdateButton().addActionListener(e -> {
            musicFolder = new File(view.getPathLabel().getText());
            dbLocation = new File(musicFolder, "libraryData/Data");
            enableView(false, "Updating the database in");
            switchToWaitingMode(true);
            new Thread(databaseUpdater).start();
        });

        view.getDisplayAllButton().addActionListener(e -> {
            // TODO: create a model for this button
        });

        view.getSearchButton().addActionListener(e -> {
            if (!Objects.equals(view.getOutputTextArea().getText(), "")) {
                view.getOutputTextArea().setText("");
            }
            musicFolder = new File(view.getPathLabel().getText());
            dbLocation = new File(musicFolder, "libraryData/Data");
            try {
                Connection con = DriverManager.getConnection("jdbc:hsqldb:file:"
                        + dbLocation, "user", "");
                ArrayList<String> searchPairs = getSearchPairs();
                if (searchPairs.size() >= 2) {
                    for (String result : DataSearch.getResults(con, searchPairs,
                            view.getFileNamesCheckBox().isSelected())) {
                        view.getOutputTextArea().append(result + "\n");
                    }
                }
            } catch (SQLException ex) {
                view.getPathLabel().setVisible(false);
                view.getMsgLabel().setText("SQL error");
            }
        });

        view.getFileNamesCheckBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                view.getTitleTextField().setEnabled(true);
            } else {
                view.getTitleTextField().setEnabled(false);
            }
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

    private Runnable databaseCreator = () -> {
        try {
            Connection con = DriverManager.getConnection("jdbc:hsqldb:file:"
                    + dbLocation, "user", "");
            MusicData.create(con, musicFolder);
            if (!MusicData.hasMp3Files(con)) {
                MusicData.delete(con, dbLocation.getParentFile());
                view.getSelectButton().setEnabled(true);
                view.getMsgLabel().setText("No MP3 files were found in");
            } else {
                enableView(true, "A new database was created in");
            }
        } catch (SQLException ex) {
            view.getPathLabel().setVisible(false);
            view.getMsgLabel().setText("SQL error");
        }
        switchToWaitingMode(false);
    };

    private Runnable databaseUpdater = () -> {
        try {
            Connection con = DriverManager.getConnection("jdbc:hsqldb:file:"
                    + dbLocation, "user", "");
            MusicData.rebuild(con, musicFolder);
            enableView(true, "The database was updated in");
        } catch (SQLException ex) {
            view.getPathLabel().setVisible(false);
            view.getMsgLabel().setText("SQL error");
        }
        switchToWaitingMode(false);
    };

    private void enableView(boolean isEnabled, String message) {
        enableComponents(view.getMainPanel(), isEnabled);
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
            view.getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            view.getMainPanel().setCursor(null);
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
}