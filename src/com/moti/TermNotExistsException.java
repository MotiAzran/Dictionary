package com.moti;

public class TermNotExistsException extends Exception {

    public TermNotExistsException(String term) {
        super(String.format("The term %s not exists", term));
    }
}
