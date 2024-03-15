package org.example.peoplehubapi.exception;

public class ImportCsvException extends RuntimeException {
    public ImportCsvException(String message, Throwable cause) {
        super(message, cause);
    }
    public ImportCsvException(String message) {
        super(message);
    }
}
