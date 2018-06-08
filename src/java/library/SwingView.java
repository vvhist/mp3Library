package library;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public final class SwingView implements View {

    private JPanel mainPanel;
    private JTextField artistField;
    private JTextField albumField;
    private JTextField genreField;
    private JTextField yearField;
    private JTextField titleField;
    private JButton searchButton;
    private JButton selectButton;
    private JButton updateButton;
    private JProgressBar progressBar;
    private JLabel msgLabel;
    private JLabel pathLabel;
    private JRadioButton titlesRadioButton;
    private JRadioButton fileNamesRadioButton;
    private JRadioButton allRadioButton;
    private JTable table;

    public SwingView() {
        Presenter presenter = new Presenter(this);

        selectButton.addActionListener(e -> presenter.select());

        updateButton.addActionListener(e -> presenter.update());

        searchButton.addActionListener(e -> {
            Map<String, String> tags = new HashMap<>();
            tags.put("Artist", artistField.getText());
            tags.put("Album",  albumField.getText());
            tags.put("Genre",  genreField.getText());
            tags.put("Year",   yearField.getText());
            tags.put("Title",  titleField.isEnabled() ? titleField.getText() : "");
            presenter.search(tags);
        });

        KeyAdapter searchOnEnter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        };
        artistField.addKeyListener(searchOnEnter);
        albumField.addKeyListener(searchOnEnter);
        genreField.addKeyListener(searchOnEnter);
        yearField.addKeyListener(searchOnEnter);
        titleField.addKeyListener(searchOnEnter);

        titlesRadioButton.addActionListener(e -> {
            titleField.setEnabled(false);
            presenter.displayTitles();
        });
        fileNamesRadioButton.addActionListener(e -> {
            titleField.setEnabled(true);
            presenter.displayFileNames();
        });
        allRadioButton.addActionListener(e -> {
            titleField.setEnabled(true);
            presenter.displayAll();
        });
        ButtonGroup group = new ButtonGroup();
        group.add(titlesRadioButton);
        group.add(fileNamesRadioButton);
        group.add(allRadioButton);

        JFrame frame = new JFrame("MP3 search");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setMinimumSize(new Dimension(500, 307));
        frame.setVisible(true);
    }

    @Override
    public void enterSelectionMode(String message) {
        enableComponents(mainPanel, false);
        selectButton.setEnabled(true);
        msgLabel.setEnabled(true);
        pathLabel.setEnabled(true);

        switchWaitingMode(false);
        msgLabel.setText(message);
    }

    @Override
    public void enterWaitingMode(String message) {
        enableComponents(mainPanel, false);
        msgLabel.setEnabled(true);
        pathLabel.setEnabled(true);

        switchWaitingMode(true);
        msgLabel.setText(message);
    }

    @Override
    public void enterSearchingMode(String message) {
        enableComponents(mainPanel, true);
        if (titlesRadioButton.isSelected()) {
            titleField.setEnabled(false);
        }
        switchWaitingMode(false);
        msgLabel.setText(message);
    }

    @Override
    public void enterExceptionMode(String message) {
        enableComponents(mainPanel, false);
        msgLabel.setEnabled(true);

        switchWaitingMode(false);
        pathLabel.setVisible(false);
        msgLabel.setText(message);
    }

    @Override
    public void displayPath(String path) {
        pathLabel.setVisible(true);
        pathLabel.setText(path);
    }

    @Override
    public void updateTable(TableModel model) {
        table.setModel(model);
    }

    private void switchWaitingMode(boolean isInWaitingMode) {
        if (isInWaitingMode) {
            mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            mainPanel.setCursor(null);
        }
        progressBar.setEnabled(isInWaitingMode);
        progressBar.setVisible(isInWaitingMode);
    }

    private void enableComponents(Container container, boolean isEnabled) {
        for (Component component : container.getComponents()) {
            component.setEnabled(isEnabled);
            if (component instanceof Container) {
                enableComponents((Container) component, isEnabled);
            }
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 10, 10), -1, -1));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        selectButton = new JButton();
        selectButton.setFocusPainted(false);
        selectButton.setText("Select");
        selectButton.setToolTipText("Select your music folder");
        toolBar1.add(selectButton);
        updateButton = new JButton();
        updateButton.setFocusPainted(false);
        updateButton.setText("Update");
        updateButton.setToolTipText("Rebuild the existing database");
        toolBar1.add(updateButton);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(10, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        artistField = new JTextField();
        panel1.add(artistField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Artist:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label2 = new JLabel();
        label2.setText("Album:");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        albumField = new JTextField();
        panel1.add(albumField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Genre:");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        genreField = new JTextField();
        panel1.add(genreField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Year:");
        panel1.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        yearField = new JTextField();
        panel1.add(yearField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Title:");
        panel1.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        titleField = new JTextField();
        panel1.add(titleField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchButton = new JButton();
        searchButton.setFocusPainted(false);
        searchButton.setText("Search");
        panel1.add(searchButton, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        titlesRadioButton = new JRadioButton();
        titlesRadioButton.setSelected(true);
        titlesRadioButton.setText("Display titles");
        panel1.add(titlesRadioButton, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileNamesRadioButton = new JRadioButton();
        fileNamesRadioButton.setText("Display file names");
        panel1.add(fileNamesRadioButton, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        allRadioButton = new JRadioButton();
        allRadioButton.setText("Display all tags");
        panel1.add(allRadioButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        msgLabel = new JLabel();
        msgLabel.setText("Select your music folder");
        mainPanel.add(msgLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathLabel = new JLabel();
        Font pathLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, pathLabel.getFont());
        if (pathLabelFont != null) pathLabel.setFont(pathLabelFont);
        pathLabel.setVisible(false);
        mainPanel.add(pathLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(300, 50), null, 0, false));
        table = new JTable();
        table.setAutoCreateRowSorter(true);
        table.setColumnSelectionAllowed(true);
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(-1118482));
        scrollPane1.setViewportView(table);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(102, 12));
        progressBar.setVisible(false);
        mainPanel.add(progressBar, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(112, 12), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}