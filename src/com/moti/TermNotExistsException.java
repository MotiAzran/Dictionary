package com.moti;

/**
 * Exception that thrown when program does
 * wrong action on term that not exists
 */
public class TermNotExistsException extends Exception {
    public TermNotExistsException(String term) {
        super(String.format("The term %s not exists", term));
    }
}
