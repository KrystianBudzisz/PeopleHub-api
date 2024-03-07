package org.example.peoplehubapi.exception;

public class UnsupportedPersonTypeException extends RuntimeException {
    public UnsupportedPersonTypeException(String message) {
        super(message);
    }
}
