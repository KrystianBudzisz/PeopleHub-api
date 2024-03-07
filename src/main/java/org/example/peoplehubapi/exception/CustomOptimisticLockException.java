package org.example.peoplehubapi.exception;

public class CustomOptimisticLockException extends RuntimeException {
    public CustomOptimisticLockException(String message) {
        super(message);
    }
}
