package com.moti;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Dictionary implements Iterable<Map.Entry<String, String>> {

    private TreeMap<String, String> _dictionary;

    public Dictionary() {
        _dictionary = new TreeMap<>();
    }

    public static Dictionary create_dictionary_from_file(Scanner dictionary_file) throws IllegalArgumentException {

        Dictionary dictionary = new Dictionary();

        while (dictionary_file.hasNext()) {
            String line = dictionary_file.nextLine();
            String term = line.split(",")[0];

            int comma_index = line.indexOf(",");
            if (-1 == comma_index) {
                // comma not found in the line
                throw new IllegalArgumentException("Invalid file format");
            }

            String explanation = line.substring(comma_index + 1);

            try {
                dictionary.add_term(term, explanation);
            } catch (TermExistsException e) {
                throw new IllegalArgumentException("Invalid file format");
            }
        }

        return dictionary;
    }

    public void add_term(String term, String explanation) throws TermExistsException {
        if (_dictionary.containsKey(term)) {
            throw new TermExistsException(term);
        }

        // Puts new term and explanation in the dictionary
        _dictionary.put(term, explanation);
    }

    public void update_term(String term, String explanation) throws TermNotExistsException {
        if (!_dictionary.containsKey(term)) {
            throw new TermNotExistsException(term);
        }

        // Update existing term in the dictionary
        _dictionary.put(term, explanation);
    }

    public void delete_term(String term) throws TermNotExistsException {
        if (!_dictionary.containsKey(term)) {
            throw new TermNotExistsException(term);
        }

        // Delete existing term in the dictionary
        _dictionary.remove(term);
    }

    public void write_to_file(FileWriter file) throws IOException {

        for (Map.Entry<String, String> e : _dictionary.entrySet()) {
            file.write(String.format("%s,%s\n", e.getKey(), e.getValue()));
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return _dictionary.entrySet().iterator();
    }
}
