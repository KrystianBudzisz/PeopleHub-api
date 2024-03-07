package org.example.peoplehubapi.exception;

public class InvalidPersonCreationException extends RuntimeException {
    public InvalidPersonCreationException(String message) {
        super(message);
    }

    public InvalidPersonCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

