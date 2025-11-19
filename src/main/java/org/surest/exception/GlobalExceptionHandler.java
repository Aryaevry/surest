package org.surest.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.surest.dto.SurestErrorResponse;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Catches exceptions thrown by controllers and returns structured JSON responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException.
     * Returns a 404 NOT FOUND response with error details.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<SurestErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        SurestErrorResponse error = SurestErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("User Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AccessDeniedException.
     * Returns a 403 FORBIDDEN response with error details.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<SurestErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        SurestErrorResponse error = SurestErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles validation errors for @Valid annotated requests.
     * Returns a 400 BAD REQUEST response with a list of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SurestErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        SurestErrorResponse error = SurestErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message(errors)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other uncaught exceptions.
     * Returns a 500 INTERNAL SERVER ERROR response with generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SurestErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
        SurestErrorResponse error = SurestErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
