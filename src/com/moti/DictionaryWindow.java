package com.moti;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

/**
 * Implement the main window of the program
 */
public class DictionaryWindow extends JFrame {
    private final int SEARCH_PANEL_HEIGHT = 50;
    private final int TEXT_AREA_HEIGHT = 400;
    private final int BUTTONS_PANEL_HEIGHT = 50;
    private final int FRAME_HEIGHT = SEARCH_PANEL_HEIGHT + TEXT_AREA_HEIGHT + BUTTONS_PANEL_HEIGHT;
    private final int FRAME_WIDTH = 600;
    private Dictionary _dictionary;
    private JMenuBar _menuBar;
    private JMenu _fileMenu;
    private JMenuItem _importMenuItem;
    private JMenuItem _exportMenuItem;
    private JMenuItem _closeMenuItem;
    private JPanel _searchPanel;
    private JTextField _searchField;
    private JButton _searchButton;
    private JScrollPane _scrollDictionary;
    private JTextArea _dictionaryText;
    private JPanel _buttonsPanel;
    private JButton _addButton;
    private JButton _updateButton;
    private JButton _removeButton;

    /**
     * Initialize window
     */
    public DictionaryWindow() {
        super("Dictionary");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        _dictionary = new Dictionary();

        _initMenu();
        _initSearchPanel();
        _initTextArea();
        initButtonsPanel();

        setJMenuBar(_menuBar);
        add(_searchPanel);
        add(_scrollDictionary);
        add(_buttonsPanel);
    }

    /**
     * Init window menu
     */
    private void _initMenu() {
        _menuBar = new JMenuBar();

        // Add file menu
        _fileMenu = new JMenu("File");
        _fileMenu.setMnemonic('F');

        // Add import option to file menu
        _importMenuItem = new JMenuItem("Import Dictionary");
        _importMenuItem.addActionListener(new MenuItemActionListener());
        _fileMenu.add(_importMenuItem);

        // Add export option to menu
        _exportMenuItem = new JMenuItem("Export Dictionary");
        _exportMenuItem.addActionListener(new MenuItemActionListener());
        _fileMenu.add(_exportMenuItem);

        // Add close option to file menu
        _closeMenuItem = new JMenuItem("Close");
        _closeMenuItem.addActionListener(new MenuItemActionListener());
        _fileMenu.add(_closeMenuItem);

        _menuBar.add(_fileMenu);
    }

    /**
     * Initialize the window search panel
     */
    private void _initSearchPanel() {
        _searchPanel = new JPanel();
        _searchPanel.setLayout(new FlowLayout());
        _searchPanel.setPreferredSize(new Dimension(FRAME_WIDTH, SEARCH_PANEL_HEIGHT));

        // Add search field
        _searchField = new JTextField();
        _searchField.setPreferredSize(new Dimension(300, 30));
        _searchPanel.add(_searchField);

        // Add search button
        _searchButton = new JButton("Search");
        _searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "";
                try {
                    String term = _searchField.getText();
                    Map.Entry<String, String> dic_term = _dictionary.getTerm(term);
                    msg = String.format("%s - %s", dic_term.getKey(), dic_term.getValue());
                } catch (TermNotExistsException exp) {
                    msg = exp.getMessage();
                }

                JOptionPane.showMessageDialog(null, msg);
            }
        });

        _searchPanel.add(_searchButton);
    }

    /**
     * Initialize dictionary text area
     */
    private void _initTextArea() {
        // Create text area
        _dictionaryText = new JTextArea();
        _dictionaryText.setPreferredSize(new Dimension(FRAME_WIDTH, TEXT_AREA_HEIGHT));
        _dictionaryText.setMaximumSize(new Dimension(FRAME_WIDTH, TEXT_AREA_HEIGHT));
        _dictionaryText.setEditable(false);

        // Add dictionary data to text area
        _resetTextArea();

        // Make the area scrollable
        _scrollDictionary = new JScrollPane(_dictionaryText);
        _scrollDictionary.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        _scrollDictionary.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Initialize window buttons:
     *  - add
     *  - update
     *  - remove
     */
    private void initButtonsPanel() {
        _buttonsPanel = new JPanel();
        _buttonsPanel.setLayout(new FlowLayout());
        _buttonsPanel.setPreferredSize(new Dimension(FRAME_WIDTH, BUTTONS_PANEL_HEIGHT));

        // Add "add button"
        _addButton = new JButton("Add term");
        _addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get term from user
                String term = JOptionPane.showInputDialog(null, "Enter term:");
                if (null == term || _dictionary.isTermExists(term)) {
                    JOptionPane.showMessageDialog(null, "Term already exists");
                    return;
                }

                // Get term explanation from user
                String explanation = JOptionPane.showInputDialog(null, "Enter explanation:");
                if (null == explanation) {
                    JOptionPane.showMessageDialog(null, "Empty explanation");
                    return;
                }

                // Add user term to dictionary
                try {
                    _dictionary.addTerm(term, explanation);
                } catch(TermExistsException exp) {
                    JOptionPane.showMessageDialog(null, exp.getMessage());
                    return;
                }

                // Show new term to window
                _resetTextArea();
            }
        });
        _buttonsPanel.add(_addButton);

        // Add update button
        _updateButton = new JButton("Update term");
        _updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get term from user
                String term = JOptionPane.showInputDialog(null, "Enter term:");
                if (null == term || !_dictionary.isTermExists(term)) {
                    JOptionPane.showMessageDialog(null, "Term not exists");
                    return;
                }

                // Get new explanation from user
                String explanation = JOptionPane.showInputDialog(null, "Enter new explanation:");
                if (null == explanation) {
                    JOptionPane.showMessageDialog(null, "Empty explanation");
                    return;
                }

                // Update term
                try {
                    _dictionary.updateTerm(term, explanation);
                } catch(TermNotExistsException exp) {
                    JOptionPane.showMessageDialog(null, exp.getMessage());
                    return;
                }

                // Show updated dictionary
                _resetTextArea();
            }
        });
        _buttonsPanel.add(_updateButton);

        // Add remove button
        _removeButton = new JButton("Remove term");
        _removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get term from user
                String term = JOptionPane.showInputDialog(null, "Enter term:");
                if (null == term || !_dictionary.isTermExists(term)) {
                    JOptionPane.showMessageDialog(null, "Term not exists");
                    return;
                }

                // Remove term from dictionary
                try {
                    _dictionary.removeTerm(term);
                } catch(TermNotExistsException exp) {
                    JOptionPane.showMessageDialog(null, exp.getMessage());
                    return;
                }

                // Show new dictionary
                _resetTextArea();
            }
        });
        _buttonsPanel.add(_removeButton);
    }

    /**
     * Write dictionary to text area
     */
    private void _resetTextArea() {
        _dictionaryText.setText("");

        // Write all terms to the text area
        for (Map.Entry<String, String> term : _dictionary) {
            _dictionaryText.append(String.format("%s - %s\n", term.getKey(), term.getValue()));
        }
    }

    /**
     * JMenu listener
     */
    private class MenuItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (_closeMenuItem.equals(e.getSource())) {
                // Close option chosen
                System.exit(0);
            }

            JFileChooser dictionaryFileChooser = new JFileChooser();
            dictionaryFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (_importMenuItem.equals(e.getSource())) {
                // Get file to import from
                int res = dictionaryFileChooser.showOpenDialog(null);
                if (JFileChooser.CANCEL_OPTION == res) {
                    return;
                }
            } else if (_exportMenuItem.equals(e.getSource())) {
                // Get file to export to
                int res = dictionaryFileChooser.showSaveDialog(null);
                if (JFileChooser.CANCEL_OPTION == res) {
                    return;
                }
            }

            // Get chosen file path
            Path file_path = dictionaryFileChooser.getSelectedFile().toPath();
            if (null == file_path) {
                JOptionPane.showMessageDialog(null, "Invalid file",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            try {
                if (_importMenuItem.equals(e.getSource())) {
                    if (!Files.exists(file_path)) {
                        JOptionPane.showMessageDialog(null, "File not found",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Import dictionary from file
                    _dictionary = Dictionary.createDictionaryFromFile(new Scanner(file_path.toFile()));
                    _resetTextArea();
                } else if (_exportMenuItem.equals(e.getSource())) {
                    FileWriter writer = new FileWriter(file_path.toFile());
                    // Export dictionary to chosen file
                    _dictionary.exportToFile(writer);
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
}
