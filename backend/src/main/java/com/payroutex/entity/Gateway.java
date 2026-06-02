package com.payroutex.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double upiSuccessRate;

    private double cardSuccessRate;

    private double netBankingSuccessRate;

    private double costPercentage;

    private String status;
}