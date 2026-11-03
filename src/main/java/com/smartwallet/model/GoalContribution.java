
package com.smartwallet.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "goal_contributions",
    indexes = {
        @Index(
            name = "idx_goal_contribution_goal",
            columnList = "goal_id"
        ),
        @Index(
            name = "idx_goal_contribution_date",
            columnList = "created_at"
        )
    }
)
public class GoalContribution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contributionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "goal_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_goal_contribution_goal"
        )
    )
    private Goal goal;

    @Column(
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal amount;

    @Column(length = 500)
    private String note;

    @Column(
        nullable = false,
        length = 20
    )
    private String source = "MANUAL";

    @Column(
        name = "reference_id",
        unique = true
    )
    private String referenceId;

    @Column(
        name = "created_at",
        nullable = false
    )
    private LocalDateTime createdAt;

    public GoalContribution() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (source == null || source.isBlank()) {
            source = "MANUAL";
        }
    }

    public Long getContributionId() {
        return contributionId;
    }

    public void setContributionId(Long contributionId) {
        this.contributionId = contributionId;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
