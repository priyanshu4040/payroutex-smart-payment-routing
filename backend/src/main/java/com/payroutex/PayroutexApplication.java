package com.payroutex;

import com.payroutex.repository.GatewayRepository;
import com.payroutex.entity.Gateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PayroutexApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayroutexApplication.class, args);
	}

	@Bean
	CommandLineRunner loadGatewayData(GatewayRepository gatewayRepository) {
		return args -> {
			gatewayRepository.save(Gateway.builder()
					.name("Razorpay")
					.upiSuccessRate(92)
					.cardSuccessRate(88)
					.netBankingSuccessRate(85)
					.costPercentage(2.0)
					.status("ACTIVE")
					.build());

			gatewayRepository.save(Gateway.builder()
					.name("Cashfree")
					.upiSuccessRate(90)
					.cardSuccessRate(91)
					.netBankingSuccessRate(87)
					.costPercentage(1.8)
					.status("ACTIVE")
					.build());

			gatewayRepository.save(Gateway.builder()
					.name("PayU")
					.upiSuccessRate(85)
					.cardSuccessRate(89)
					.netBankingSuccessRate(90)
					.costPercentage(1.5)
					.status("ACTIVE")
					.build());

			gatewayRepository.save(Gateway.builder()
					.name("Stripe")
					.upiSuccessRate(70)
					.cardSuccessRate(94)
					.netBankingSuccessRate(80)
					.costPercentage(2.5)
					.status("DOWN")
					.build());
		};
	}
}