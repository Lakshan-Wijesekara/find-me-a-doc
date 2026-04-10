package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.*;
import com.findmeadoc.application.ports.DoctorRegistrationUseCase;
import com.findmeadoc.application.ports.UserLoginUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final DoctorRegistrationUseCase doctorRegistrationUseCase;
    private final UserLoginUseCase userLoginUseCase;

    // Constructor Injection
    public AuthController(
            DoctorRegistrationUseCase doctorRegistrationUseCase,
            UserLoginUseCase userLoginUseCase) {
        this.doctorRegistrationUseCase = doctorRegistrationUseCase;
        this.userLoginUseCase = userLoginUseCase;
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<DoctorRegistrationResponse> registerDoctor(@RequestBody DoctorRegistrationRequest request) {
        DoctorRegistrationResponse response = doctorRegistrationUseCase.registerDoctor(request); // Calling the use case
        // Returns 201 status along with the generated profile data
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userLoginUseCase.logUser(request); // Calling the use case
        return ResponseEntity.ok(response); // Returns 200 with token
    }
}