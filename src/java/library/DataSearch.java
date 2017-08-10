package library;

import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.Objects;

public class DataSearch {

    private static boolean isSuitable(TagEntry entry, String[] searchQuery) {
        for (int i = 0; i < searchQuery.length - 1; i += 2) {
            String key   = searchQuery[i];
            String value = searchQuery[i + 1];
            if (!Objects.equals("NoSuchKey",  entry.getTag(key))
                    && !Objects.equals(value, entry.getTag(key))) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Object> getResults(Database database, CommandLine cmd) {
        ArrayList<Object> selectedData = new ArrayList<>();

        for (TagEntry entry : database.getData()) {
            if (isSuitable(entry, cmd.getOptionValues("search"))) {
                if (!cmd.hasOption("path") && entry.getTag("Title") != null) {
                             selectedData.add(entry.getTag("Title"));
                } else {
                             selectedData.add(entry.getTag("FileName"));
                }
            }
        }
        return selectedData;
    }
}