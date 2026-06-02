package com.payroutex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AnalyticsResponse {

    private long totalPayments;

    private long successfulPayments;

    private long failedPayments;

    private double successRate;

    private double totalRevenue;
}