package com.smartwallet.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AIConversationDetailResponse(
        String id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AIConversationMessageResponse> messages) {
}
