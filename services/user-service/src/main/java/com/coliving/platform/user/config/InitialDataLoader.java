package com.coliving.platform.user.config;

import com.coliving.platform.user.entity.Role;
import com.coliving.platform.user.entity.UserEmailAccountEntity;
import com.coliving.platform.user.entity.UserEntity;
import com.coliving.platform.user.repository.EmailAccountRepository;
import com.coliving.platform.user.repository.UserRepository;
import com.coliving.platform.user.service.ContractAutocompleteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Profile("dev")
@Component
public class InitialDataLoader implements CommandLineRunner {

    private final UserRepository memberRepository;
    private final EmailAccountRepository emailAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContractAutocompleteService contractAutocompleteService;

    @Value("${INITIAL_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${INITIAL_ADMIN_PASSWORD:}")
    private String adminPassword;

    public InitialDataLoader(
            UserRepository memberRepository,
            EmailAccountRepository emailAccountRepository,
            PasswordEncoder passwordEncoder,
            ContractAutocompleteService contractAutocompleteService) {
        this.memberRepository = memberRepository;
        this.emailAccountRepository = emailAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.contractAutocompleteService = contractAutocompleteService;
    }

    @Override
    public void run(String... args) {

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        if (emailAccountRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        UserEntity member = memberRepository.save(UserEntity.builder()
                .roles(Set.of(Role.ADMIN))
                .build());

        contractAutocompleteService.save(
                member, adminEmail, null, null, null, null);

        emailAccountRepository.save(UserEmailAccountEntity.builder()
                .user(member)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .build());
    }
}