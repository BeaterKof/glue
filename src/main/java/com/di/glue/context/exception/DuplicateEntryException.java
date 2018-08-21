package com.di.glue.context.exception;

public class DuplicateEntryException extends Exception {

    private static final String MSG = "Duplicate key/value pair for: ";

    public DuplicateEntryException(String message) {
        super(message);
    }

    public DuplicateEntryException(Object key, Object subKey, Object value) {
        super(MSG + key + " -> " + subKey + " (" + value + ")");
    }

}
