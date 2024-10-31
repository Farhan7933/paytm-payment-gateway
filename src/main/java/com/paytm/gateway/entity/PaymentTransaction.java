package com.paytm.gateway.entity;

import com.wfe.rapid.data.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_transactions")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Long orderId;

    @Column(name = "transactionToken")
    private String transactionToken;

    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "email", nullable = false)
    private User customer;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column(name = "paytmTransactionId")
    private String paytmTransactionId;

    @Column(name = "status")
    private String transactionStatus;

    @Column(name = "gatewayName")
    private String gatewayName;

    @Column(name = "currency")
    private String currency;

    @Column(name = "transactionDate")
    private String transactionDate;

    @Column(name = "paymentMode")
    private String paymentMode;

    @Column(name = "bankTransactionId")
    private String bankTransactionId;

    @Column(name = "bankName")
    private String bankName;

    @Column(name = "createdAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
