package com.test;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableHystrixDashboard
public class HystrixApplication {


	public static void main(String[] args) {
		SpringApplication.run(HystrixApplication.class, args);
	}

}
