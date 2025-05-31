package com.velazco.velazco_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VelazcoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VelazcoBackendApplication.class, args);
	}

}
