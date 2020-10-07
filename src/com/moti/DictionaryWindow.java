package com.moti;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

public class DictionaryWindow extends JFrame {

    private final int SEARCH_PANEL_HEIGHT = 50;
    private final int TEXT_AREA_HEIGHT = 400;
    private final int BUTTONS_PANEL_HEIGHT = 50;
    private final int FRAME_HEIGHT = SEARCH_PANEL_HEIGHT + TEXT_AREA_HEIGHT + BUTTONS_PANEL_HEIGHT;
    private final int FRAME_WIDTH = 600;
    private Dictionary _dictionary;
    private JMenuBar _menu_bar;
    private JMenu _file_menu;
    private JMenuItem _import_menu_item;
    private JMenuItem _export_menu_item;
    private JMenuItem _close_menu_item;
    private JPanel _search_panel;
    private JTextField _search_field;
    private JButton _search_button;
    private JScrollPane _scroll_dictionary;
    private JTextArea _dictionary_text;
    private JPanel _buttons_panel;
    private JButton _add_button;
    private JButton _update_button;
    private JButton _remove_button;

    DictionaryWindow() {
        super("Dictionary");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        _dictionary = new Dictionary();

        _init_menu();
        _init_search_panel();
        _init_text_area();
        _init_buttons_panel();

        setJMenuBar(_menu_bar);
        add(_search_panel);
        add(_scroll_dictionary);
        add(_buttons_panel);
    }

    private void _init_menu() {
        _menu_bar = new JMenuBar();

        _file_menu = new JMenu("File");
        _file_menu.setMnemonic('F');

        _import_menu_item = new JMenuItem("Import Dictionary");
        _import_menu_item.addActionListener(new MenuItemActionListener());
        _file_menu.add(_import_menu_item);

        _export_menu_item = new JMenuItem("Export Dictionary");
        _export_menu_item.addActionListener(new MenuItemActionListener());
        _file_menu.add(_export_menu_item);

        _close_menu_item = new JMenuItem("Close");
        _close_menu_item.addActionListener(new MenuItemActionListener());
        _file_menu.add(_close_menu_item);

        _menu_bar.add(_file_menu);
    }

    private void _init_search_panel() {
        _search_panel = new JPanel();
        _search_panel.setLayout(new FlowLayout());
        _search_panel.setPreferredSize(new Dimension(FRAME_WIDTH, SEARCH_PANEL_HEIGHT));

        _search_field = new JTextField();
        _search_field.setPreferredSize(new Dimension(300, 30));
        _search_panel.add(_search_field);

        _search_button = new JButton("Search");
        _search_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "";
                try {
                    String term = _search_field.getText();
                    Map.Entry<String, String> dic_term = _dictionary.get_term(term);
                    msg = String.format("%s - %s", dic_term.getKey(), dic_term.getValue());
                } catch (TermNotExistsException exp) {
                    msg = exp.getMessage();
                }

                JOptionPane.showMessageDialog(null, msg);
            }
        });

        _search_panel.add(_search_button);
    }

    private void _init_text_area() {

        _dictionary_text = new JTextArea();
        _dictionary_text.setPreferredSize(new Dimension(FRAME_WIDTH, TEXT_AREA_HEIGHT));
        _dictionary_text.setMaximumSize(new Dimension(FRAME_WIDTH, TEXT_AREA_HEIGHT));
        _dictionary_text.setEditable(false);

        _reset_text_area();

        _scroll_dictionary = new JScrollPane(_dictionary_text);
        _scroll_dictionary.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        _scroll_dictionary.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private void _init_buttons_panel() {

        _buttons_panel = new JPanel();
        _buttons_panel.setLayout(new FlowLayout());
        _buttons_panel.setPreferredSize(new Dimension(FRAME_WIDTH, BUTTONS_PANEL_HEIGHT));

        _add_button = new JButton("Add term");
        _add_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String term = JOptionPane.showInputDialog(null, "Enter term:");
                if (_dictionary.is_term_exists(term)) {
                    JOptionPane.showMessageDialog(null, "Term already exists");
                    return;
                }

                String explanation = JOptionPane.showInputDialog(null, "Enter explanation:");

                try {
                    _dictionary.add_term(term, explanation);
                } catch(TermExistsException exp) {
                    JOptionPane.showMessageDialog(null, exp.getMessage());
                    return;
                }

                _reset_text_area();
            }
        });
        _buttons_panel.add(_add_button);

        _update_button = new JButton("Update term");
        _update_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String term = JOptionPane.showInputDialog(null, "Enter term:");
                if (!_dictionary.is_term_exists(term)) {
                    JOptionPane.showMessageDialog(null, "Term not exists");
                    return;
                }

                String explanation = JOptionPane.showInputDialog(null, "Enter new explanation:");

                try {
                    _dictionary.update_term(term, explanation);
                } catch(TermNotExistsException exp) {
                    JOptionPane.showMessageDialog(null, exp.getMessage());
                    return;
                }

                _reset_text_area();
            }
        });
        _buttons_panel.add(_update_button);

        _remove_button = new JButton("Remove term");
        _remove_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String term = JOptionPane.showInputDialog(null, "Enter term:");
                if (!_dictionary.is_term_exists(term)) {
                    JOptionPane.showMessageDialog(null, "Term not exists");
                    return;
                }

                try {
                    _dictionary.remove_term(term);
                } catch(TermNotExistsException exp) {
                    JOptionPane.showMessageDialog(null, exp.getMessage());
                    return;
                }

                _reset_text_area();
            }
        });
        _buttons_panel.add(_remove_button);
    }

    private void _reset_text_area() {

        _dictionary_text.setText("");

        // Write all terms to the text area
        for (Map.Entry<String, String> term : _dictionary) {
            _dictionary_text.append(String.format("%s - %s\n", term.getKey(), term.getValue()));
        }
    }

    private class MenuItemActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (_close_menu_item.equals(e.getSource())) {
                System.exit(0);
            }

            JFileChooser dictionary_file_chooser = new JFileChooser();
            dictionary_file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (_import_menu_item.equals(e.getSource())) {
                int res = dictionary_file_chooser.showOpenDialog(null);
                if (JFileChooser.CANCEL_OPTION == res) {
                    return;
                }
            } else if (_export_menu_item.equals(e.getSource())) {
                int res = dictionary_file_chooser.showSaveDialog(null);
                if (JFileChooser.CANCEL_OPTION == res) {
                    return;
                }
            }

            Path file_path = dictionary_file_chooser.getSelectedFile().toPath();
            if (null == file_path) {
                JOptionPane.showMessageDialog(null, "Invalid file",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            try {
                if (_import_menu_item.equals(e.getSource())) {
                    if (!Files.exists(file_path)) {
                        JOptionPane.showMessageDialog(null, "File not found",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    _dictionary = Dictionary.create_dictionary_from_file(new Scanner(file_path.toFile()));
                    _reset_text_area();
                } else if (_export_menu_item.equals(e.getSource())) {
                    FileWriter writer = new FileWriter(file_path.toFile());
                    _dictionary.write_to_file(writer);
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
