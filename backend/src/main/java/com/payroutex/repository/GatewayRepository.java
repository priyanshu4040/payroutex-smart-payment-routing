package com.payroutex.repository;

import com.payroutex.entity.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GatewayRepository extends JpaRepository<Gateway, Long> {

    List<Gateway> findByStatus(String status);
}