package com.smartwallet.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestResponseLoggingFilter
        extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    RequestResponseLoggingFilter.class
            );

    private static final String REQUEST_ID =
            "requestId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestId =
                UUID.randomUUID().toString();

        long startTime =
                System.currentTimeMillis();

        try {

            // ----------------------------------------------------
            // REQUEST ID
            // ----------------------------------------------------

            MDC.put(
                    REQUEST_ID,
                    requestId
            );

            response.setHeader(
                    "X-Request-ID",
                    requestId
            );

            // ----------------------------------------------------
            // REQUEST LOG
            // ----------------------------------------------------

            logger.info(
                    "Request started: {} {}",
                    request.getMethod(),
                    request.getRequestURI()
            );

            // ----------------------------------------------------
            // CONTINUE REQUEST
            // ----------------------------------------------------

            filterChain.doFilter(
                    request,
                    response
            );

        } finally {

            // ----------------------------------------------------
            // RESPONSE LOG
            // ----------------------------------------------------

            long duration =
                    System.currentTimeMillis()
                    - startTime;

            logger.info(
                    "Request completed: {} {} - Status: {} - Duration: {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );

            // ----------------------------------------------------
            // CLEAN MDC
            // ----------------------------------------------------

            MDC.remove(REQUEST_ID);
        }
    }
}