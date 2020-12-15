/**
 * Represent dictionary term
 */
public class Term implements Comparable<Term> {
    private String term;
    private String explanation;

    /**
     * Initialize term with given
     * term and explanation
     * @param term term to initialize to
     * @param explanation explanation to initialize
     */
    public Term(String term, String explanation) {
        this.term = term;
        this.explanation = explanation;
    }

    /**
     * Get term
     * @return object term
     */
    public String getTerm() {
        return term;
    }

    /**
     * Modify object term
     * @param term term to modify to
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * Get object explanation
     * @return object explanation
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Modify object explanation
     * @param explanation explanation to modify to
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     * compare this term to other term
     * @param other term to compare
     * @return 0 if the terms are equal
     * positive number if this term is greater
     * negative number if this term is smaller
     */
    @Override
    public int compareTo(Term other) {
        return term.compareTo(other.term);
    }

    /**
     * Get string representation of term
     * @return string representation of term
     */
    @Override
    public String toString() {
        return String.format("%s: %s", term, explanation);
    }
}
