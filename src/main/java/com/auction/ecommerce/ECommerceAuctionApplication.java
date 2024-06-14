package com.auction.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ECommerceAuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceAuctionApplication.class, args);
	}

}