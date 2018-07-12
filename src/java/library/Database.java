package library;

import org.hsqldb.jdbc.JDBCPool;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Database {

    private final File location;
    private final JDBCPool pool = new JDBCPool();

    public Database(File location) {
        this.location = location;
        pool.setURL("jdbc:hsqldb:file:" + new File(location, "Data"));
        pool.setUser("user");
        pool.setPassword("");
    }

    public void create(List<DataEntry> entries) throws SQLException {
        try (Connection connection = pool.getConnection("user", "");
             Statement statement = connection.createStatement()) {

            statement.execute("SET IGNORECASE TRUE");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS mp3Library("
                    + "id       INT IDENTITY PRIMARY KEY,"
                    + "filename VARCHAR(1000) NOT NULL,"
                    + "artist   VARCHAR(1000),"
                    + "title    VARCHAR(1000),"
                    + "album    VARCHAR(1000),"
                    + "genre    VARCHAR(1000),"
                    + "year     INT)"
            );
            String sql = "INSERT INTO mp3Library (filename, artist, title, album, genre, year)"
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preStatement = connection.prepareStatement(sql)) {
                for (DataEntry entry : entries) {
                    preStatement.setString(1, entry.getFilename());
                    preStatement.setString(2, entry.getArtist());
                    preStatement.setString(3, entry.getTitle());
                    preStatement.setString(4, entry.getAlbum());
                    preStatement.setString(5, entry.getGenre());
                    if (entry.getYear() == null) {
                        preStatement.setNull(6, Types.INTEGER);
                    } else {
                        preStatement.setInt(6, entry.getYear());
                    }
                    preStatement.executeUpdate();
                }
            }
        }
    }

    public List<DataEntry> search(Map<String, String> searchFilter) throws SQLException {
        String sql = "SELECT * FROM mp3Library WHERE " + createConditions(searchFilter);
        try (Connection connection = pool.getConnection("user", "");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<DataEntry> filteredEntries = new ArrayList<>();
            while (resultSet.next()) {
                String filename = resultSet.getString("filename");
                String artist   = resultSet.getString("artist");
                String title    = resultSet.getString("title");
                String album    = resultSet.getString("album");
                String genre    = resultSet.getString("genre");
                int resYear     = resultSet.getInt("year");
                Integer year = (resYear == 0) ? null : resYear;

                filteredEntries.add(new DataEntry(filename, artist, title, album, genre, year));
            }
            return filteredEntries;
        }
    }

    public void clear() throws SQLException {
        try (Connection connection = pool.getConnection("user", "");
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DROP TABLE IF EXISTS mp3Library");
        }
    }

    public void delete() throws SQLException {
        try (Connection connection = pool.getConnection("user", "");
             Statement statement = connection.createStatement()) {

            statement.execute("SHUTDOWN");
            if (location.exists()) {
                deleteFolder(location);
            }
        }
    }

    private String createConditions(Map<String, String> searchValues) {
        searchValues.replaceAll((key, value) -> "'" + value + "'");
        return searchValues.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(" AND "));
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}