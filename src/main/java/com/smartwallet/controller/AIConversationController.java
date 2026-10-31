package com.smartwallet.controller;

import com.smartwallet.dto.*;
import com.smartwallet.service.AIConversationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/conversations")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class AIConversationController {

    private final AIConversationService conversationService;

    public AIConversationController(AIConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AIConversationSummaryResponse create(
            @Valid @RequestBody(required = false)
                    CreateAIConversationRequest request,
            Authentication authentication) {
        String title = request == null ? null : request.title();
        return conversationService.create(authentication.getName(), title);
    }

    @GetMapping
    public List<AIConversationSummaryResponse> list(
            Authentication authentication) {
        return conversationService.list(authentication.getName());
    }

    @GetMapping("/{conversationId}")
    public AIConversationDetailResponse get(
            @PathVariable String conversationId,
            Authentication authentication) {
        return conversationService.get(
                authentication.getName(), conversationId);
    }

    @PatchMapping("/{conversationId}")
    public AIConversationSummaryResponse rename(
            @PathVariable String conversationId,
            @Valid @RequestBody RenameAIConversationRequest request,
            Authentication authentication) {
        return conversationService.rename(
                authentication.getName(), conversationId, request.title());
    }

    @DeleteMapping("/{conversationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String conversationId,
            Authentication authentication) {
        conversationService.delete(authentication.getName(), conversationId);
    }
}
