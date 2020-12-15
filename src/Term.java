public class Term implements Comparable<Term> {
    private String term;
    private String explanation;

    public Term(String term, String explanation) {
        this.term = term;
        this.explanation = explanation;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation() {
        this.explanation = explanation;
    }

    @Override
    public int compareTo(Term other) {
        return term.compareTo(other.term);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", term, explanation);
    }
}
