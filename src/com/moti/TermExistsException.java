package com.moti;

/**
 * Exception that thrown when program does
 * wrong action on existed term
 */
public class TermExistsException extends Exception {
    public TermExistsException(String term) {
        super(String.format("The term %s already exists", term));
    }
}
