package org.example.peoplehubapi.exception;

public class InvalidPersonUpdateException extends RuntimeException {

    public InvalidPersonUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
