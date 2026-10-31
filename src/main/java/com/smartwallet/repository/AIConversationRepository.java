package com.smartwallet.repository;

import com.smartwallet.model.AIConversation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIConversationRepository
        extends JpaRepository<AIConversation, String> {

    Page<AIConversation> findByUser_EmailOrderByUpdatedAtDesc(
            String email, Pageable pageable);

    Optional<AIConversation> findByIdAndUser_Email(
            String id, String email);
}
