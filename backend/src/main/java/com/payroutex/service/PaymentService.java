package com.payroutex.service;

import com.payroutex.dto.AnalyticsResponse;
import com.payroutex.dto.PaymentRequest;
import com.payroutex.entity.Gateway;
import com.payroutex.entity.Transaction;
import com.payroutex.repository.GatewayRepository;
import com.payroutex.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PaymentService {

    private final GatewayRepository gatewayRepository;
    private final TransactionRepository transactionRepository;

    public PaymentService(
            GatewayRepository gatewayRepository,
            TransactionRepository transactionRepository
    ) {
        this.gatewayRepository = gatewayRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction makePayment(PaymentRequest request) {

        List<Gateway> activeGateways = gatewayRepository.findByStatus("ACTIVE");

        // If all gateways are down, save failed transaction
        if (activeGateways.isEmpty()) {

            Transaction failedTransaction = Transaction.builder()
                    .customerName(request.getCustomerName())
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod().toUpperCase())
                    .bankName(request.getBankName())
                    .selectedGateway("NONE")
                    .fallbackGateway(null)
                    .status("FAILED")
                    .reason("Payment failed because all gateways are down")
                    .createdAt(LocalDateTime.now())
                    .build();

            return transactionRepository.save(failedTransaction);
        }

        List<Gateway> sortedGateways = activeGateways.stream()
                .sorted(Comparator.comparingDouble(
                        gateway -> -calculateScore(gateway, request.getPaymentMethod())
                ))
                .toList();

        Gateway selectedGateway = sortedGateways.get(0);

        boolean paymentSuccess = simulatePayment(selectedGateway, request.getPaymentMethod());

        String fallbackGatewayName = null;
        String finalStatus;
        String reason;

        if (paymentSuccess) {
            finalStatus = "SUCCESS";
            reason = "Payment successful using best gateway: " + selectedGateway.getName();
        } else {
            if (sortedGateways.size() > 1) {
                Gateway fallbackGateway = sortedGateways.get(1);
                fallbackGatewayName = fallbackGateway.getName();

                boolean fallbackSuccess = simulatePayment(fallbackGateway, request.getPaymentMethod());

                if (fallbackSuccess) {
                    finalStatus = "SUCCESS";
                    reason = "Primary gateway failed. Payment completed using fallback gateway: "
                            + fallbackGateway.getName();
                } else {
                    finalStatus = "FAILED";
                    reason = "Both primary and fallback gateways failed";
                }
            } else {
                finalStatus = "FAILED";
                reason = "Primary gateway failed and no fallback gateway available";
            }
        }

        Transaction transaction = Transaction.builder()
                .customerName(request.getCustomerName())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod().toUpperCase())
                .bankName(request.getBankName())
                .selectedGateway(selectedGateway.getName())
                .fallbackGateway(fallbackGatewayName)
                .status(finalStatus)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    private double calculateScore(Gateway gateway, String paymentMethod) {

        double successRate = getSuccessRate(gateway, paymentMethod);

        double costPenalty = gateway.getCostPercentage() * 5;

        return successRate - costPenalty;
    }

    private boolean simulatePayment(Gateway gateway, String paymentMethod) {

        double successRate = getSuccessRate(gateway, paymentMethod);

        double randomNumber = Math.random() * 100;

        return randomNumber <= successRate;
    }

    private double getSuccessRate(Gateway gateway, String paymentMethod) {

        return switch (paymentMethod.toUpperCase()) {
            case "UPI" -> gateway.getUpiSuccessRate();
            case "CARD" -> gateway.getCardSuccessRate();
            case "NET_BANKING" -> gateway.getNetBankingSuccessRate();
            default -> 0;
        };
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public AnalyticsResponse getAnalytics() {

        List<Transaction> transactions = transactionRepository.findAll();

        long totalPayments = transactions.size();

        long successfulPayments = transactions.stream()
                .filter(t -> t.getStatus().equals("SUCCESS"))
                .count();

        long failedPayments = transactions.stream()
                .filter(t -> t.getStatus().equals("FAILED"))
                .count();

        double successRate = totalPayments == 0
                ? 0
                : ((double) successfulPayments / totalPayments) * 100;

        double totalRevenue = transactions.stream()
                .filter(t -> t.getStatus().equals("SUCCESS"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new AnalyticsResponse(
                totalPayments,
                successfulPayments,
                failedPayments,
                successRate,
                totalRevenue
        );
    }
}