package com.moti;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represent a word dictionary
 */
public class Dictionary implements Iterable<Map.Entry<String, String>> {
    private TreeMap<String, String> _dictionary;

    /**
     * Initialize empty dictionary
     */
    public Dictionary() {
        _dictionary = new TreeMap<>();
    }

    /**
     * Create dictionary from file.
     * The file format is that each line contains
     * <term>,<explanation>
     * @param dictionary_file where to get the dictionary input
     * @return Dictionary initialized from the file
     * @throws IllegalArgumentException thrown when the file is with invalid format
     */
    public static Dictionary createDictionaryFromFile(Scanner dictionary_file) throws IllegalArgumentException {
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
                dictionary.addTerm(term, explanation);
            } catch (TermExistsException e) {
                throw new IllegalArgumentException("Invalid file format");
            }
        }

        return dictionary;
    }

    /**
     * Add term to the dictionary
     * @param term term to add
     * @param explanation term explanation
     * @throws TermExistsException The term already exists
     */
    public void addTerm(String term, String explanation) throws TermExistsException {
        if (_dictionary.containsKey(term)) {
            throw new TermExistsException(term);
        }

        // Puts new term and explanation in the dictionary
        _dictionary.put(term, explanation);
    }

    /**
     * Update existing dictionary term
     * @param term term to update
     * @param explanation new explanation
     * @throws TermNotExistsException thrown when the term not exists
     */
    public void updateTerm(String term, String explanation) throws TermNotExistsException {
        if (!_dictionary.containsKey(term)) {
            throw new TermNotExistsException(term);
        }

        // Update existing term in the dictionary
        _dictionary.put(term, explanation);
    }

    /**
     * Remove existing term
     * @param term term to remove
     * @throws TermNotExistsException thrown when the term not exists
     */
    public void removeTerm(String term) throws TermNotExistsException {
        if (!_dictionary.containsKey(term)) {
            throw new TermNotExistsException(term);
        }

        // Delete existing term in the dictionary
        _dictionary.remove(term);
    }

    /**
     * Get term element, get the term and it's explanation
     * @param term term to get
     * @return term and it's explanation
     * @throws TermNotExistsException throw when the requested term isn't exists
     */
    public Map.Entry<String, String> getTerm(String term) throws TermNotExistsException {
        if (!_dictionary.containsKey(term)) {
            throw new TermNotExistsException(term);
        }

        // Delete existing term in the dictionary
        for (Map.Entry<String, String> entry : _dictionary.entrySet()) {
            if (term.equalsIgnoreCase(entry.getKey())) {
                return entry;
            }
        }

        throw new TermNotExistsException(term);
    }

    /**
     * Checks if term exists
     * @param term term to search
     * @return true if the term exists, otherwise false
     */
    public boolean isTermExists(String term) {
        return _dictionary.containsKey(term);
    }

    /**
     * Export dictionary to file
     * @param file File to export the file
     * @throws IOException thrown in case of file error
     */
    public void exportToFile(FileWriter file) throws IOException {
        for (Map.Entry<String, String> entry : _dictionary.entrySet()) {
            file.write(String.format("%s,%s\n", entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Implement dictionary iterator
     * @return iterator to dictionary items
     */
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return _dictionary.entrySet().iterator();
    }
}
