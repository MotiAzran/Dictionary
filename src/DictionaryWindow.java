import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Implement the main window of the program
 */
public class DictionaryWindow extends JFrame {
    private final int SEARCH_FIELD_HEIGHT = 30;
    private final int SEARCH_PANEL_HEIGHT = 50;
    private final int TEXT_AREA_HEIGHT = 400;
    private final int BUTTONS_PANEL_HEIGHT = 50;
    private final int FRAME_HEIGHT = SEARCH_PANEL_HEIGHT + TEXT_AREA_HEIGHT + BUTTONS_PANEL_HEIGHT;
    private final int FRAME_WIDTH = 600;
    private Dictionary dictionary;
    private JMenuBar bar;
    private JMenu fileMenu;
    private JMenuItem importMenuItem;
    private JMenuItem exportMenuItem;
    private JMenuItem closeMenuItem;
    private JPanel searchPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JScrollPane scrollDictionary;
    private JTextArea dictionaryText;
    private JPanel buttonsPanel;
    private JButton addButton;
    private JButton updateButton;
    private JButton removeButton;

    /**
     * Initialize window
     */
    public DictionaryWindow() {
        super("Dictionary");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        dictionary = new Dictionary();

        initMenu();
        initSearchPanel();
        initTextArea();
        initButtonsPanel();

        setJMenuBar(bar);
        add(searchPanel);
        add(scrollDictionary);
        add(buttonsPanel);
    }

    /**
     * Init window menu
     */
    private void initMenu() {
        bar = new JMenuBar();

        // Add file menu
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        // Add import option to file menu
        importMenuItem = new JMenuItem("Import Dictionary");
        importMenuItem.addActionListener(new MenuItemActionListener());
        fileMenu.add(importMenuItem);

        // Add export option to menu
        exportMenuItem = new JMenuItem("Export Dictionary");
        exportMenuItem.addActionListener(new MenuItemActionListener());
        fileMenu.add(exportMenuItem);

        // Add close option to file menu
        closeMenuItem = new JMenuItem("Close");
        closeMenuItem.addActionListener(new MenuItemActionListener());
        fileMenu.add(closeMenuItem);

        bar.add(fileMenu);
    }

    /**
     * Initialize the window search panel
     */
    private void initSearchPanel() {
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.setPreferredSize(new Dimension(FRAME_WIDTH, SEARCH_PANEL_HEIGHT));

        // Add search field
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(FRAME_WIDTH / 2, SEARCH_FIELD_HEIGHT));
        searchPanel.add(searchField);

        // Add search button
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "";
                try {
                    String term = searchField.getText();
                    Term dictionaryTerm = dictionary.getTerm(term);
                    msg = dictionaryTerm.toString();
                } catch (TermNotExistsException exp) {
                    msg = exp.getMessage();
                }

                JOptionPane.showMessageDialog(DictionaryWindow.this, msg);
            }
        });

        searchPanel.add(searchButton);
    }

    /**
     * Initialize dictionary text area
     */
    private void initTextArea() {
        // Create text area
        dictionaryText = new JTextArea();
        dictionaryText.setPreferredSize(new Dimension(FRAME_WIDTH, TEXT_AREA_HEIGHT));
        dictionaryText.setMaximumSize(new Dimension(FRAME_WIDTH, TEXT_AREA_HEIGHT));
        dictionaryText.setEditable(false);

        // Add dictionary data to text area
        resetTextArea();

        // Make the area scrollable
        scrollDictionary = new JScrollPane(dictionaryText);
        scrollDictionary.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDictionary.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Initialize window buttons:
     *  - add
     *  - update
     *  - remove
     */
    private void initButtonsPanel() {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.setPreferredSize(new Dimension(FRAME_WIDTH, BUTTONS_PANEL_HEIGHT));

        // Add "add button"
        addButton = new JButton("Add term");
        addButton.addActionListener(new ControlButtonsActionListener());
        buttonsPanel.add(addButton);

        // Add update button
        updateButton = new JButton("Update term");
        updateButton.addActionListener(new ControlButtonsActionListener());
        buttonsPanel.add(updateButton);

        // Add remove button
        removeButton = new JButton("Remove term");
        removeButton.addActionListener(new ControlButtonsActionListener());
        buttonsPanel.add(removeButton);
    }

    /**
     * Write dictionary to text area
     */
    private void resetTextArea() {
        dictionaryText.setText("");

        // Write all terms to the text area
        for (Term term : dictionary) {
            dictionaryText.append(String.format("%s\n\n", term));
        }
    }

    /**
     * JMenu listener
     */
    private class MenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (closeMenuItem.equals(e.getSource())) {
                // Close option chosen
                System.exit(0);
            }

            JFileChooser dictionaryFileChooser = new JFileChooser();
            dictionaryFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (importMenuItem.equals(e.getSource())) {
                // Get file to import from
                int res = dictionaryFileChooser.showOpenDialog(DictionaryWindow.this);
                if (JFileChooser.CANCEL_OPTION == res) {
                    return;
                }
            } else if (exportMenuItem.equals(e.getSource())) {
                // Get file to export to
                int res = dictionaryFileChooser.showSaveDialog(DictionaryWindow.this);
                if (JFileChooser.CANCEL_OPTION == res) {
                    return;
                }
            }

            // Get chosen file path
            Path filePath = dictionaryFileChooser.getSelectedFile().toPath();
            if (null == filePath) {
                JOptionPane.showMessageDialog(null, "Invalid file",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            try {
                if (importMenuItem.equals(e.getSource())) {
                    if (!Files.exists(filePath)) {
                        JOptionPane.showMessageDialog(null, "File not found",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Import dictionary from file
                    dictionary = Dictionary.createDictionaryFromFile(new Scanner(filePath.toFile()));
                    resetTextArea();
                } else if (exportMenuItem.equals(e.getSource())) {
                    FileWriter writer = new FileWriter(filePath.toFile());
                    // Export dictionary to chosen file
                    dictionary.exportToFile(writer);
                    writer.close();
                }
            } catch (FileNotFoundException exp) {
                JOptionPane.showMessageDialog(null, "File not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException exp) {
                JOptionPane.showMessageDialog(null, exp.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ControlButtonsActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton) e.getSource();

            if (btn.equals(addButton)) {
                handleAdd();
            } else if (btn.equals(updateButton)) {
                handleUpdate();
            } else if (btn.equals(removeButton)) {
                handleRemove();
            }
        }

        private String showExplanationInputDialog() {
            final JTextArea text = new JTextArea("", 20, 40);
            JOptionPane pane = new JOptionPane(new Object[] { "Enter explanation:",
                    new JScrollPane(text) }, JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION);

            pane.setWantsInput(false);
            JDialog dialog = pane.createDialog(DictionaryWindow.this, "");
            dialog.pack();
            dialog.setVisible(true);
            Integer value = (Integer) pane.getValue();
            if (value == null || value == JOptionPane.CANCEL_OPTION
                    || value == JOptionPane.CLOSED_OPTION) {
                return "";
            }

            return text.getText();
        }

        private void handleAdd() {
            // Get term from user
            String term = JOptionPane.showInputDialog(DictionaryWindow.this, "Enter term:");
            if (null == term || dictionary.isTermExists(term)) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, "Term already exists");
                return;
            }

            String explanation = showExplanationInputDialog();
            if (null == explanation || explanation.isEmpty()) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, "Empty explanation");
                return;
            }

            // Add user term to dictionary
            try {
                dictionary.addTerm(term, explanation);
            } catch(TermExistsException exp) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, exp.getMessage());
                return;
            }

            // Show new term to window
            resetTextArea();
        }

        private void handleUpdate() {
            // Get term from user
            String term = JOptionPane.showInputDialog(DictionaryWindow.this, "Enter term:");
            if (null == term || !dictionary.isTermExists(term)) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, "Term not exists");
                return;
            }

            // Get new explanation from user
            String explanation = showExplanationInputDialog();
            if (null == explanation || explanation.isEmpty()) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, "Empty explanation");
                return;
            }

            // Update term
            try {
                dictionary.updateTerm(term, explanation);
            } catch(TermNotExistsException exp) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, exp.getMessage());
                return;
            }

            // Show updated dictionary
            resetTextArea();
        }

        private void handleRemove() {
            // Get term from user
            String term = JOptionPane.showInputDialog(DictionaryWindow.this, "Enter term:");
            if (null == term || !dictionary.isTermExists(term)) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, "Term not exists");
                return;
            }

            // Remove term from dictionary
            try {
                dictionary.removeTerm(term);
            } catch(TermNotExistsException exp) {
                JOptionPane.showMessageDialog(DictionaryWindow.this, exp.getMessage());
                return;
            }

            // Show new dictionary
            resetTextArea();
        }
    }
}
