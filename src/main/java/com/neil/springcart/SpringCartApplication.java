package com.neil.springcart;

import com.neil.springcart.model.Admin;
import com.neil.springcart.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCartApplication.class, args);
	}

	// For dev purposes, a fake admin is being added to the database on startup
	// Usually an admin would be added manually, but it's an effort to add an
	// admin manually each time the server is started
	@Bean
	@Profile("!test")
	public CommandLineRunner dbInit(AdminRepository adminRepository,
									PasswordEncoder passwordEncoder) {
		return (args) -> {
			Admin admin = Admin.builder()
					.email("admin@springcart.com")
					.password(passwordEncoder.encode("password"))
					.build();
			adminRepository.save(admin);
		};
	}

}
