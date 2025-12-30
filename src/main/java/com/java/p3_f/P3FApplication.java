package com.java.p3_f;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.java.p3_f.inits.MainInit;

@SpringBootApplication
@EnableScheduling
public class P3FApplication {

	public static void main(String[] args) {
		MainInit.mainInit();
		SpringApplication.run(P3FApplication.class, args);
	}

}
