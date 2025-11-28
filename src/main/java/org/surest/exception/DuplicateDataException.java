package org.surest.exception;

/**
 * Custom exception thrown when a User is not found in the system.
 */
public class DuplicateDataException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicateDataException(String message) {
        super(message);
    }

}
