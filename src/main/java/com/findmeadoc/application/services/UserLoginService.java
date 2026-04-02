package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.LoginRequest;
import com.findmeadoc.application.dto.LoginResponse;
import com.findmeadoc.application.ports.TokenProvider;
import com.findmeadoc.application.ports.UserLoginUseCase;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService implements UserLoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    //Injecting at the constructor
    public UserLoginService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public LoginResponse logUser(LoginRequest request) {
        // Find the user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Compare the passwords
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate the JWT Token

        String token = this.tokenProvider.generateToken(user);

        // Return the LoginResponse
        return new LoginResponse(user.getEmail(), token, user.getId(), user.getRole().name());
    }
}