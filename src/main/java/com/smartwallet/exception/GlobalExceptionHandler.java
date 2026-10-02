package com.smartwallet.exception;

import com.smartwallet.dto.ApiResponse;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    // ============================================================
    // RESOURCE NOT FOUND
    // ============================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleResourceNotFound(
            ResourceNotFoundException ex) {

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    // ============================================================
    // BAD REQUEST
    // ============================================================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleBadRequest(
            BadRequestException ex) {

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // ============================================================
    // ACCESS DENIED
    // ============================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleAccessDenied(
            AccessDeniedException ex) {

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        "Access Denied",
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    // ============================================================
    // VALIDATION ERROR
    // ============================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleValidationException(
            MethodArgumentNotValidException ex) {

        String message =
                "Invalid request data.";

        if (!ex.getBindingResult()
                .getFieldErrors()
                .isEmpty()) {

            String validationMessage =
                    ex.getBindingResult()
                            .getFieldErrors()
                            .get(0)
                            .getDefaultMessage();

            if (validationMessage != null
                    && !validationMessage.isBlank()) {

                message = validationMessage;
            }
        }

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        message,
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // ============================================================
    // WALLET NOT FOUND
    // ============================================================

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleWalletNotFound(
            WalletNotFoundException ex) {

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    // ============================================================
    // AI SERVICE ERROR
    // ============================================================

    /**
     * Handles AI request validation and AI-specific
     * application errors.
     *
     * Examples:
     * - Empty/invalid AI question
     * - Prompt injection attempt
     * - Unsupported AI instruction
     * - Invalid financial context
     * - Invalid AI response
     */
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleAIServiceException(
            AIServiceException ex) {

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    // ============================================================
    // GENERAL / UNEXPECTED EXCEPTION
    // ============================================================

    /**
     * Handles unexpected application errors.
     *
     * IMPORTANT:
     * Detailed exception information is logged on the server,
     * but is never returned to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>>
    handleGeneralException(
            Exception ex) {

        logger.error(
                "Unexpected application error occurred.",
                ex
        );

        ApiResponse<Void> response =
                new ApiResponse<>(
                        false,
                        "An internal server error occurred.",
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}