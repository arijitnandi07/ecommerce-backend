package com.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ecommerce")
public class ECommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceApplication.class, args);
		System.out.println("=================================");
		System.out.println("E-Commerce Application Started!");
		System.out.println("API Base URL: http://localhost:8080/api/v1");
		System.out.println("=================================");
	}
}