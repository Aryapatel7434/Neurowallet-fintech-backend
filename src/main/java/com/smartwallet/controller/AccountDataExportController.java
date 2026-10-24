
package com.smartwallet.controller;

import com.smartwallet.dto.AccountDataExportResponse;
import com.smartwallet.security.AccountExportRateLimiter;
import com.smartwallet.service.AccountDataExportService;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users/me")
public class AccountDataExportController {

    private final AccountDataExportService exportService;
    private final AccountExportRateLimiter rateLimiter;

    public AccountDataExportController(
            AccountDataExportService exportService,
            AccountExportRateLimiter rateLimiter) {
        this.exportService = exportService;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping(
            value = "/data-export",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AccountDataExportResponse> exportMyData(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        String email = authentication.getName();

        if (!rateLimiter.isAllowed(email)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Export limit reached. Please try again later."
            );
        }

        AccountDataExportResponse export =
                exportService.exportMyAccountData(email);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(export);
    }
}
