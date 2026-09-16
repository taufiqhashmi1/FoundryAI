package com.taufiqhashmi.foundryai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class FoundryaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoundryaiApplication.class, args);
		System.out.println("FoundryAI Application started successfully!");
	}

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
