package library;

import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.Objects;

public class DataSearch {

    private static boolean isSuitable(int index, Database database, String[] searchQuery) {
        for (int i = 0; i < searchQuery.length - 1; i += 2) {
            String key   = searchQuery[i];
            String value = searchQuery[i + 1];
            if (database.data.containsKey(key)
                    && !Objects.equals(value, database.data.get(key).get(index))) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Object> getResults(Database database, CommandLine cmd) {
        ArrayList<Object> selectedData = new ArrayList<>();
        ArrayList fileNames = database.data.get("FileName");
        ArrayList titles    = database.data.get("Title");

        for (int i = 0; i < fileNames.size(); i++) {
            if (isSuitable(i, database, cmd.getOptionValues("search"))) {
                if (!cmd.hasOption("path") && titles.get(i) != null) {
                             selectedData.add(titles.get(i));
                } else {
                             selectedData.add(fileNames.get(i));
                }
            }
        }
        return selectedData;
    }
}