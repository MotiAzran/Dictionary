import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represent a word dictionary
 */
public class Dictionary implements Iterable<Term> {
    private TreeSet<Term> dictionary;

    /**
     * Initialize empty dictionary
     */
    public Dictionary() {
        dictionary = new TreeSet<Term>();
    }

    /**
     * Create dictionary from file.
     * The file format is:
     * <term1>explanation</term1>
     * <term2>
     * multi line
     * explanation
     * </term2>
     * ...
     * @param dictionaryFile where to get the dictionary input
     * @return Dictionary initialized from the file
     * @throws IllegalArgumentException thrown when the file is with invalid format
     */
    public static Dictionary createDictionaryFromFile(Scanner dictionaryFile) throws IllegalArgumentException {
        if (null == dictionaryFile) {
            throw new IllegalArgumentException("null argument");
        }

        Dictionary dictionary = new Dictionary();

        while (dictionaryFile.hasNext()) {
            try {
                dictionary.addTerm(getTermFromFile(dictionaryFile));
            } catch (TermExistsException e) {
                throw new IllegalArgumentException("Invalid file format");
            }
        }

        return dictionary;
    }

    /**
     * Get term and explanation from file
     * @param dictionaryFile where to get the explanation from
     * @return The term and explanation from the file
     * @throws IllegalArgumentException thrown when the file is with invalid format
     */
    private static Term getTermFromFile(Scanner dictionaryFile) throws IllegalArgumentException {
        String result = "";
        boolean isTermFound = false;
        while (dictionaryFile.hasNextLine()) {
            result = dictionaryFile.nextLine();
            if (result.contains("<") && result.contains("</") && result.contains(">")) {
                isTermFound = true;
                break;
            }
        }

        if (!isTermFound) {
            throw new IllegalArgumentException("Invalid file format");
        }

        String term = result.substring(result.indexOf('<') + 1, result.indexOf('>'));
        String closingTerm = result.substring(result.indexOf("</") + 2, result.lastIndexOf('>'));

        if (!term.equals(closingTerm)) {
            throw new IllegalArgumentException("Invalid file format");
        }

        String explanation = result.substring(result.indexOf('>') + 1, result.indexOf("</"));

        return new Term(term, explanation);
    }

    /**
     * Add term to the dictionary
     * @param term term to add
     * @param explanation term explanation
     * @throws TermExistsException The term already exists
     */
    public void addTerm(String term, String explanation) throws TermExistsException {
        addTerm(new Term(term, explanation));
    }

    /**
     * Add term to the dictionary
     * @param term term to add
     * @throws TermExistsException The term already exists
     */
    public void addTerm(Term term) throws TermExistsException {
        if (null == term) {
            throw new IllegalArgumentException("null argument");
        }

        if (dictionary.contains(term)) {
            throw new TermExistsException(term.getTerm());
        }

        // Puts new term in the dictionary
        dictionary.add(term);
    }

    /**
     * Update existing dictionary term
     * @param term term to update
     * @param explanation new explanation
     * @throws TermNotExistsException thrown when the term not exists
     */
    public void updateTerm(String term, String explanation) throws TermNotExistsException {
        updateTerm(getTerm(term), explanation);
    }

    /**
     * Update existing dictionary term
     * @param term term to update
     * @param explanation new explanation
     * @throws TermNotExistsException thrown when the term not exists
     */
    public void updateTerm(Term term, String explanation) throws TermNotExistsException {
        if (!dictionary.contains(term)) {
            throw new TermNotExistsException(term.getTerm());
        }

        dictionary.remove(term);

        Term newTerm = new Term(term.getTerm(), explanation);

        dictionary.add(newTerm);
    }

    /**
     * Remove existing term
     * @param term term to remove
     * @throws TermNotExistsException thrown when the term not exists
     */
    public void removeTerm(String term) throws TermNotExistsException {
        removeTerm(getTerm(term));
    }

    /**
     * Remove existing term
     * @param term term to remove
     * @throws TermNotExistsException thrown when the term not exists
     */
    public void removeTerm(Term term) throws TermNotExistsException {
        if (!dictionary.contains(term)) {
            throw new TermNotExistsException(term.getTerm());
        }

        // Delete existing term in the dictionary
        dictionary.remove(term);
    }

    /**
     * Get term element, get the term and it's explanation
     * @param term term to get
     * @return term object
     * @throws TermNotExistsException throw when the requested term isn't exists
     */
    public Term getTerm(String term) throws TermNotExistsException {
        for (Term t : dictionary) {
            if (term.equals(t.getTerm())) {
                return t;
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
        try {
            getTerm(term);
        } catch (TermNotExistsException e) {
            return false;
        }

        return true;
    }

    /**
     * Export dictionary to file
     * @param file File to export the file
     * @throws IOException thrown in case of file error
     */
    public void exportToFile(FileWriter file) throws IOException {
        for (Term term : dictionary) {
            file.write(String.format("<%s>%s</%s>\n", term.getTerm(), term.getExplanation(), term.getTerm()));
        }
    }

    /**
     * Implement dictionary iterator
     * @return iterator to dictionary items
     */
    @Override
    public Iterator<Term> iterator() {
        return dictionary.iterator();
    }
}
