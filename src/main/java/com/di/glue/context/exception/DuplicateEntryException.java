package com.di.glue.context.exception;

public class DuplicateEntryException extends RuntimeException {

    private static final String MSG = "Duplicate key/value pair for: %s %s";

    public DuplicateEntryException(String message) {
        super(message);
    }

    public DuplicateEntryException(String key, String value) {
        super("Duplicate key/value pair for: " + key + " -> " + value);
    }

    public DuplicateEntryException(Class<?> key, Class<?> value) {
        super("Duplicate key/value pair for: " + key.toString() + " -> " + value.toString());
    }
}
