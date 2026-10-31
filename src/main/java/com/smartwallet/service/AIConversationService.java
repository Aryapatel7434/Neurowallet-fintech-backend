package com.smartwallet.service;

import com.smartwallet.dto.*;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.model.AIConversation;
import com.smartwallet.model.AIConversationMessage;
import com.smartwallet.model.User;
import com.smartwallet.repository.AIConversationMessageRepository;
import com.smartwallet.repository.AIConversationRepository;
import com.smartwallet.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AIConversationService {

    private static final int MAX_CONVERSATIONS_PER_PAGE = 50;

    private final AIConversationRepository conversationRepository;
    private final AIConversationMessageRepository messageRepository;
    private final UserRepository userRepository;

    public AIConversationService(
            AIConversationRepository conversationRepository,
            AIConversationMessageRepository messageRepository,
            UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AIConversationSummaryResponse create(String authenticatedEmail,
                                                String requestedTitle) {
        User user = requireUser(authenticatedEmail);
        String title = normalizeTitle(requestedTitle);
        AIConversation conversation =
                conversationRepository.save(new AIConversation(user, title));
        return toSummary(conversation, 0);
    }

    @Transactional(readOnly = true)
    public List<AIConversationSummaryResponse> list(String authenticatedEmail) {
        return conversationRepository
                .findByUser_EmailOrderByUpdatedAtDesc(
                        authenticatedEmail,
                        PageRequest.of(0, MAX_CONVERSATIONS_PER_PAGE,
                                Sort.by(Sort.Direction.DESC, "updatedAt")))
                .stream()
                .map(c -> toSummary(c,
                        messageRepository.countByConversation_Id(c.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public AIConversationDetailResponse get(String authenticatedEmail,
                                             String conversationId) {
        AIConversation conversation = requireOwnedConversation(
                authenticatedEmail, conversationId);
        List<AIConversationMessageResponse> messages =
                messageRepository
                        .findByConversation_IdOrderByCreatedAtAscIdAsc(
                                conversationId)
                        .stream()
                        .map(this::toMessage)
                        .toList();

        return new AIConversationDetailResponse(
                conversation.getId(), conversation.getTitle(),
                conversation.getCreatedAt(), conversation.getUpdatedAt(),
                messages);
    }

    @Transactional
    public AIConversationSummaryResponse rename(String authenticatedEmail,
                                                 String conversationId,
                                                 String requestedTitle) {
        AIConversation conversation = requireOwnedConversation(
                authenticatedEmail, conversationId);
        conversation.setTitle(normalizeRequiredTitle(requestedTitle));
        AIConversation saved = conversationRepository.save(conversation);
        return toSummary(saved,
                messageRepository.countByConversation_Id(conversationId));
    }

    @Transactional
    public void delete(String authenticatedEmail, String conversationId) {
        AIConversation conversation = requireOwnedConversation(
                authenticatedEmail, conversationId);
        conversationRepository.delete(conversation);
    }

    private User requireUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Authenticated user was not found");
        }
        return user;
    }

    private AIConversation requireOwnedConversation(String email, String id) {
        if (!StringUtils.hasText(id)) {
            throw new ResourceNotFoundException("Conversation was not found");
        }
        return conversationRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation was not found"));
    }

    private String normalizeTitle(String title) {
        if (!StringUtils.hasText(title)) return "New conversation";
        return normalizeRequiredTitle(title);
    }

    private String normalizeRequiredTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BadRequestException("Conversation title is required");
        }
        String normalized = title.trim();
        if (normalized.length() > 120) {
            throw new BadRequestException(
                    "Conversation title cannot exceed 120 characters");
        }
        return normalized;
    }

    private AIConversationSummaryResponse toSummary(
            AIConversation conversation, long messageCount) {
        return new AIConversationSummaryResponse(
                conversation.getId(), conversation.getTitle(),
                conversation.getCreatedAt(), conversation.getUpdatedAt(),
                messageCount);
    }

    private AIConversationMessageResponse toMessage(
            AIConversationMessage message) {
        return new AIConversationMessageResponse(
                message.getId(), message.getRole().name(),
                message.getContent(), message.getCreatedAt());
    }
}
