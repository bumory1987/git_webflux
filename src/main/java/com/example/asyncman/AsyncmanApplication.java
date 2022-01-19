package com.example.asyncman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class AsyncmanApplication {

	@Bean
	public ThreadPoolTaskExecutor myThreadPool(){
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(1);
		te.setMaxPoolSize(10);
		te.initialize();
		return te;
	}

	public static void main(String[] args) {
		SpringApplication.run(AsyncmanApplication.class, args);
	}

}
