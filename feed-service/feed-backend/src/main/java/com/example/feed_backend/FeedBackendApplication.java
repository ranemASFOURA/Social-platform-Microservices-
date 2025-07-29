package com.example.feed_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FeedBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedBackendApplication.class, args);
	}

}
