package com.findmeadoc.infrastructure.exceptions;
import com.findmeadoc.application.dto.ErrorResponse;
import com.findmeadoc.domain.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice // Safety net which catches thrown exceptions anywhere in controllers or services
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        // 2. Build our custom error payload
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // 404
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                ex.getMessage(), // "Doctor not found with id: 5"
                request.getDescription(false).replace("uri=", "") // e.g., "/api/v1/appointments"
        );

        // 3. Send it back to the client
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }
}
