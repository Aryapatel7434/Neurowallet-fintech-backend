
package com.smartwallet.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "budgets",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_budget_user_category_period",
            columnNames = {
                "user_id",
                "category",
                "period_start",
                "period_end"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_budget_user",
            columnList = "user_id"
        ),
        @Index(
            name = "idx_budget_user_status",
            columnList = "user_id, status"
        ),
        @Index(
            name = "idx_budget_period",
            columnList = "period_start, period_end"
        )
    }
)
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_budget_user")
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 40
    )
    private TransactionCategory category;

    @Column(
        name = "budget_name",
        nullable = false,
        length = 100
    )
    private String budgetName;

    @Column(
        name = "limit_amount",
        nullable = false,
        precision = 19,
        scale = 2
    )
    private BigDecimal limitAmount;

    @Column(
        name = "alert_threshold",
        nullable = false
    )
    private Integer alertThreshold = 80;

    @Column(
        name = "period_start",
        nullable = false
    )
    private LocalDate periodStart;

    @Column(
        name = "period_end",
        nullable = false
    )
    private LocalDate periodEnd;

    @Column(
        nullable = false,
        length = 20
    )
    private String status = "ACTIVE";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Budget() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (alertThreshold == null) {
            alertThreshold = 80;
        }

        if (status == null || status.isBlank()) {
            status = "ACTIVE";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Integer getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}
