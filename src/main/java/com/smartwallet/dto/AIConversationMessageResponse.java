package com.smartwallet.dto;

import java.time.LocalDateTime;

public record AIConversationMessageResponse(
        Long id,
        String role,
        String content,
        LocalDateTime createdAt) {
}
