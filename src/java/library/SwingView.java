package library;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class SwingView {

    private JTextField artistTextField;
    private JTextField albumTextField;
    private JTextField genreTextField;
    private JTextField yearTextField;
    private JTextField titleTextField;
    private JTextArea outputTextArea;
    private JPanel mainPanel;
    private JButton searchButton;
    private JCheckBox fileNamesCheckBox;
    private JButton selectButton;
    private JLabel msgLabel;
    private JButton updateButton;
    private JButton displayAllButton;
    private JLabel pathLabel;
    private JProgressBar progressBar;

    SwingView() {
        JFrame frame = new JFrame("MP3 search");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JTextField getArtistTextField() {
        return artistTextField;
    }

    public JTextField getAlbumTextField() {
        return albumTextField;
    }

    public JTextField getGenreTextField() {
        return genreTextField;
    }

    public JTextField getYearTextField() {
        return yearTextField;
    }

    public JTextField getTitleTextField() {
        return titleTextField;
    }

    public JTextArea getOutputTextArea() {
        return outputTextArea;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JCheckBox getFileNamesCheckBox() {
        return fileNamesCheckBox;
    }

    public JButton getSelectButton() {
        return selectButton;
    }

    public JLabel getMsgLabel() {
        return msgLabel;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JButton getDisplayAllButton() {
        return displayAllButton;
    }

    public JLabel getPathLabel() {
        return pathLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
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
        displayAllButton = new JButton();
        displayAllButton.setFocusPainted(false);
        displayAllButton.setText("Display all");
        displayAllButton.setToolTipText("Display the database as a table");
        toolBar1.add(displayAllButton);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        artistTextField = new JTextField();
        panel1.add(artistTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 24), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Artist:");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(47, 16), null, 1, false));
        final JLabel label2 = new JLabel();
        label2.setText("Album:");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(47, 16), null, 1, false));
        albumTextField = new JTextField();
        panel1.add(albumTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 24), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Genre:");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(47, 16), null, 1, false));
        genreTextField = new JTextField();
        panel1.add(genreTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 24), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Year:");
        panel1.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(47, 16), null, 1, false));
        yearTextField = new JTextField();
        panel1.add(yearTextField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 24), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Title:");
        panel1.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(47, 16), null, 1, false));
        titleTextField = new JTextField();
        panel1.add(titleTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 24), null, 0, false));
        fileNamesCheckBox = new JCheckBox();
        fileNamesCheckBox.setText("Display file names instead of titles");
        panel1.add(fileNamesCheckBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 22), null, 0, false));
        searchButton = new JButton();
        searchButton.setFocusPainted(false);
        searchButton.setText("Search");
        panel1.add(searchButton, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 22), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        msgLabel = new JLabel();
        msgLabel.setText("Select your music folder");
        mainPanel.add(msgLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathLabel = new JLabel();
        Font pathLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, pathLabel.getFont());
        if (pathLabelFont != null) pathLabel.setFont(pathLabelFont);
        pathLabel.setVisible(false);
        mainPanel.add(pathLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(300, -1), null, null, 0, false));
        outputTextArea = new JTextArea();
        Font outputTextAreaFont = this.$$$getFont$$$("Segoe UI", -1, 12, outputTextArea.getFont());
        if (outputTextAreaFont != null) outputTextArea.setFont(outputTextAreaFont);
        scrollPane1.setViewportView(outputTextArea);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(102, 12));
        progressBar.setVisible(false);
        mainPanel.add(progressBar, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 12), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}