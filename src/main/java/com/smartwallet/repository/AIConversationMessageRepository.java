package com.smartwallet.repository;

import com.smartwallet.model.AIConversationMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIConversationMessageRepository
        extends JpaRepository<AIConversationMessage, Long> {

    List<AIConversationMessage> findByConversation_IdOrderByCreatedAtAscIdAsc(
            String conversationId);

    long countByConversation_Id(String conversationId);
}
