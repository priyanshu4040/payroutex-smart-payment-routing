package com.payroutex.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private double amount;

    private String paymentMethod;

    private String bankName;

    private String selectedGateway;

    private String fallbackGateway;

    private String status;

    private String reason;

    private LocalDateTime createdAt;
}