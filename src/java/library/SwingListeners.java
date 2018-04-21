package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SwingListeners {

    private SwingView view;
    private String column = "Title";
    private LibraryData library;

    public SwingListeners(SwingView swingView) {
        this.view = swingView;
        enableView(false, "Select your music folder");
        view.getSelectButton().setEnabled(true);

        view.getSelectButton().addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = chooser.showDialog(null, "Select");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                library = new LibraryData(chooser.getSelectedFile());
                view.getPathLabel().setVisible(true);
                view.getPathLabel().setText(library.getMusicFolder().getPath());
                if (!library.getDataLocation().exists()) {
                    enableView(false, "Creating a database in");
                    switchToWaitingMode(true);
                    new DatabaseCreator("A new database was created in").execute();
                } else {
                    try {
                        library.establishConnection();
                    } catch (SQLException e1) {
                        view.getPathLabel().setVisible(false);
                        view.getMsgLabel().setText("SQL error");
                        e1.printStackTrace();
                    }
                    enableView(true, "Search in");
                }
            }
        });

        view.getUpdateButton().addActionListener(e -> {
            try {
                library.delete();
            } catch (SQLException e1) {
                view.getPathLabel().setVisible(false);
                view.getMsgLabel().setText("SQL error");
                e1.printStackTrace();
            }
            enableView(false, "Updating the database in");
            switchToWaitingMode(true);
            new DatabaseCreator("The database was updated in").execute();
        });

        view.getSearchButton().addActionListener(e -> {
            try {
                List<String> searchPairs = getSearchPairs();
                if (searchPairs.size() >= 2) {
                    view.getTable().setModel(createTableModel(searchPairs));
                }
            } catch (SQLException ex) {
                view.getPathLabel().setVisible(false);
                view.getMsgLabel().setText("SQL error");
                ex.printStackTrace();
            }
        });

        view.getTitlesRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(false);
            column = "Title";
        });

        view.getFileNamesRadioButton().addActionListener(e -> {
            view.getTitleTextField().setEnabled(true);
            column = "Filename";
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

    private class DatabaseCreator extends SwingWorker<Boolean, Void> {

        String message;

        DatabaseCreator(String message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
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
                    enableView(true, message);
                }
                get();
            } catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof SQLException) {
                    view.getPathLabel().setVisible(false);
                    view.getMsgLabel().setText("SQL error");
                }
                e.printStackTrace();
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

    private List<String> getSearchPairs() {
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

    private DefaultTableModel createTableModel(List<String> searchPairs) throws SQLException {
        Object[][] data = new DataSearch(column).getResults(searchPairs);
        Object[] columnNames;
        if (Objects.equals(column, "all")) {
            String[] tagNames = DataEntry.getTagNames();
            columnNames = Arrays.copyOfRange(tagNames, 1, tagNames.length);
        } else {
            columnNames = new String[] {column};
        }
        return new DefaultTableModel(data, columnNames);
    }
}