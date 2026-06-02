package com.payroutex.controller;

import com.payroutex.dto.AnalyticsResponse;
import com.payroutex.dto.PaymentRequest;
import com.payroutex.entity.Transaction;
import com.payroutex.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public Transaction makePayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.makePayment(request);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return paymentService.getAllTransactions();
    }

    @GetMapping("/analytics")
    public AnalyticsResponse getAnalytics() {
        return paymentService.getAnalytics();
    }
}