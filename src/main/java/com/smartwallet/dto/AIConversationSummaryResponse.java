package com.smartwallet.dto;

import java.time.LocalDateTime;

public record AIConversationSummaryResponse(
        String id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long messageCount) {
}
