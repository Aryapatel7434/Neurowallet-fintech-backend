package com.smartwallet.dto;

import jakarta.validation.constraints.Size;

public record CreateAIConversationRequest(
        @Size(max = 120, message = "Title cannot exceed 120 characters")
        String title) {
}
