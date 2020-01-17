package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ComputeServiceApplication2 {
	public static void main(String[] args) {
		SpringApplication.run(ComputeServiceApplication2.class, args);
	}
}