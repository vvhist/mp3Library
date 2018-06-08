package library;

import javax.swing.table.TableModel;

public interface View {

    void enterSelectionMode(String message);

    void enterWaitingMode(String message);

    void enterSearchingMode(String message);

    void enterExceptionMode(String message);

    void displayPath(String path);

    void updateTable(TableModel model);
}
