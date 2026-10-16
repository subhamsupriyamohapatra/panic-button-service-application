package com.emergency.panicbutton.config;

import com.emergency.panicbutton.common.ApiError;
import com.emergency.panicbutton.common.ValidationErrorItem;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Shaped to match the running FastAPI service's custom error envelope:
 * {@code {"success": false, "message": ..., "errorCode": ..., "details": ...}}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_CODE = "VALIDATION_ERROR";
    private static final String VALIDATION_MESSAGE = "Input validation error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex) {

        Class<?> targetClass = ex.getBindingResult().getTarget() == null
                ? ex.getParameter().getParameterType()
                : ex.getBindingResult().getTarget().getClass();

        Map<String, Integer> order = ValidationErrorFactory.declarationOrder(targetClass);

        List<ValidationErrorItem> details = ex.getBindingResult().getFieldErrors()
                .stream()
                .sorted(Comparator.comparingInt(
                        field -> order.getOrDefault(field.getField(), Integer.MAX_VALUE)))
                .map(field -> ValidationErrorFactory.forField(
                        targetClass,
                        field.getField(),
                        field.getCode(),
                        field.getDefaultMessage(),
                        field.getRejectedValue()))
                .toList();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiError(VALIDATION_CODE, VALIDATION_MESSAGE, details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(
            ConstraintViolationException ex) {

        List<ValidationErrorItem> details = ex.getConstraintViolations().stream()
                .map(violation -> ValidationErrorFactory.forQuery(
                        lastNode(violation.getPropertyPath().toString()),
                        pydanticMessage(violation.getMessage())))
                .toList();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiError(VALIDATION_CODE, VALIDATION_MESSAGE, details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable() {
        List<ValidationErrorItem> details = List.of(
                ValidationErrorFactory.unreadable());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiError(VALIDATION_CODE, VALIDATION_MESSAGE, details));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex) {
        return messageOrDefault(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ApiError> handleNoHandler() {
        return messageOrDefault(null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed() {
        return messageOrDefault(null, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex) {
        return messageOrDefault(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
            ResponseStatusException ex) {
        return messageOrDefault(ex.getReason(), HttpStatus.valueOf(
                ex.getStatusCode().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return messageOrDefault(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> messageOrDefault(String message, HttpStatus status) {
        String resolved = (message == null || message.isBlank())
                ? status.getReasonPhrase()
                : message;
        return ResponseEntity.status(status)
                .body(ApiError.of("HTTP_" + status.value(), resolved));
    }

    private String lastNode(String propertyPath) {
        int idx = propertyPath.lastIndexOf('.');
        return idx >= 0 ? propertyPath.substring(idx + 1) : propertyPath;
    }

    private String pydanticMessage(String beanMessage) {
        if (beanMessage == null) {
            return "Invalid value";
        }
        if (beanMessage.contains("greater than or equal to")) {
            return "Input should be " + beanMessage;
        }
        if (beanMessage.contains("less than or equal to")) {
            return "Input should be " + beanMessage;
        }
        return beanMessage;
    }
}
