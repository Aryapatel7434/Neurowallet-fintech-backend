package com.smartwallet.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//This class work for all controllers
@RestControllerAdvice//this tells spring this class handles exception globally
public class GlobalExceptionHandler {
   //Handle only ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)

    public ResponseEntity<ErrorResponse>
    handleResourceNotFound(ResourceNotFoundException ex) {
     //contain actual exception object
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(Exception.class)
 //This method handle all type Exception
public ResponseEntity<ErrorResponse>
handleGeneralException(Exception ex) {

    ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage()
    );

    return new ResponseEntity<>(
            error,
            HttpStatus.INTERNAL_SERVER_ERROR
    );
}
@ExceptionHandler(BadRequestException.class)
public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {

    ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage()
    );

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}
}