package com.payroutex.controller;

import com.payroutex.entity.Gateway;
import com.payroutex.service.GatewayService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateways")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping
    public List<Gateway> getAllGateways() {
        return gatewayService.getAllGateways();
    }

    @PutMapping("/{id}/status")
    public Gateway updateGatewayStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return gatewayService.updateGatewayStatus(id, status);
    }
}