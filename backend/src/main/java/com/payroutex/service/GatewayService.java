package com.payroutex.service;

import com.payroutex.entity.Gateway;
import com.payroutex.repository.GatewayRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatewayService {

    private final GatewayRepository gatewayRepository;

    public GatewayService(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public List<Gateway> getAllGateways() {
        return gatewayRepository.findAll();
    }

    public Gateway updateGatewayStatus(Long id, String status) {
        Gateway gateway = gatewayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gateway not found"));

        gateway.setStatus(status.toUpperCase());

        return gatewayRepository.save(gateway);
    }
}