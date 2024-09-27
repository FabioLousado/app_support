package com.pst.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
public class SupportApplication {
	

	public static void main(String[] args) {
		
		SpringApplication.run(SupportApplication.class, args);
	}

}
