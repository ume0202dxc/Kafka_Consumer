package com.kafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

/**
 * 
 * This class will run the Spring Boot Application for Kafka consumer.
 * 
 */

@EnableAdminServer
@SpringBootApplication
public class App extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(App.class, args);
	}
}
