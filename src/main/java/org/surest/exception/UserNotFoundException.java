package org.surest.exception;

/**
 * Custom exception thrown when a User is not found in the system.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
