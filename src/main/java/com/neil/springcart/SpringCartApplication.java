package com.neil.springcart;

import com.neil.springcart.model.Admin;
import com.neil.springcart.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Slf4j
public class SpringCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCartApplication.class, args);
		log.info("Swagger docs available on /swagger-ui/index.html");
	}

	// For dev purposes, a fake admin is being added to the database on startup
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
