package org.surest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Standard structure for all error responses returned by the API.
 * Includes a builder for clean and fluent creation.
 */
public class SurestErrorResponse {

    /**
     * Timestamp of when the error occurred.
     * Formatted as "yyyy-MM-dd HH:mm:ss".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /** HTTP status code (e.g., 404, 403, 500). */
    private int status;

    /** Short description of the error (e.g., "User Not Found"). */
    private String error;

    /** Detailed error message explaining the reason. */
    private String message;

    /** The URI path that caused the error. */
    private String path;

    // Private constructor for builder usage
    private SurestErrorResponse() {}

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    /**
     * Builder class for creating ErrorResponse instances fluently.
     */
    public static class Builder {
        private final SurestErrorResponse response;

        public Builder() {
            response = new SurestErrorResponse();
            response.timestamp = LocalDateTime.now(); // default timestamp
        }

        /** Set HTTP status code */
        public Builder status(int status) {
            response.status = status;
            return this;
        }

        /** Set short error description */
        public Builder error(String error) {
            response.error = error;
            return this;
        }

        /** Set detailed error message */
        public Builder message(String message) {
            response.message = message;
            return this;
        }

        /** Set URI path of the request that caused the error */
        public Builder path(String path) {
            response.path = path;
            return this;
        }

        /** Build the ErrorResponse instance */
        public SurestErrorResponse build() {
            return response;
        }
    }

    /**
     * Convenience method to get a new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }
}
