package library;

public interface View {

    void enterSelectionMode(String message);

    void enterWaitingMode(String message);

    void enterSearchingMode(String message);

    void enterExceptionMode(String message);

    void displayPath(String path);

    void updateTable(String[][] data, String[] columns);
}