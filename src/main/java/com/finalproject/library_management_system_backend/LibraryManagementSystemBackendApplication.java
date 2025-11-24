package com.finalproject.library_management_system_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryManagementSystemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementSystemBackendApplication.class, args);
	}

}
