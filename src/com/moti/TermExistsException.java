package com.moti;

public class TermExistsException extends Exception {

    public TermExistsException(String term) {
        super(String.format("The term %s already exists", term));
    }
}
