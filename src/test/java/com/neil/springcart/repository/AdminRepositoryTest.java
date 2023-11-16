package com.neil.springcart.repository;

import com.neil.springcart.model.Admin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AdminRepositoryTest {
    @Autowired
    private AdminRepository adminRepository;

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void findByEmail_itShouldReturnAdminIfAdminWithEmailExists() {
        // Given an admin with the email admin@springcart.com exists
        String email = "admin@springcart.com";
        Admin admin = createAdminWithEmail(email);
        adminRepository.save(admin);
        // When findByEmail() is called
        Optional<Admin> adminInDb = adminRepository.findByEmail(email);
        // Then result is an Admin object
        assertThat(adminInDb).isNotEmpty();
    }

    @Test
    void findByEmail_itShouldReturnAnEmptyObjectIfAdminWithEmailDoesNotExist() {
        // Given an admin with the email admin@springcart.com exists
        String email = "admin@springcart.com";
        // When findByEmail() is called
        Optional<Admin> adminInDb = adminRepository.findByEmail(email);
        // Then the result is empty
        assertThat(adminInDb).isEmpty();
    }

    private Admin createAdminWithEmail(String email) {
        return Admin.builder()
                .email(email)
                .password("password")
                .build();
    }
}