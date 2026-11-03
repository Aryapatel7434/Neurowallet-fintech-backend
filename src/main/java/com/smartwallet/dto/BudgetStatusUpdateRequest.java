
package com.smartwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BudgetStatusUpdateRequest {

    @NotBlank(message = "Budget status is required")
    @Pattern(
        regexp = "ACTIVE|PAUSED|CANCELLED",
        message = "Status must be ACTIVE, PAUSED or CANCELLED"
    )
    private String status;

    public BudgetStatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
