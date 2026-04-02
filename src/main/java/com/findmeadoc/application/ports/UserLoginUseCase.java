package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.LoginRequest;
import com.findmeadoc.application.dto.LoginResponse;

public interface UserLoginUseCase {
    LoginResponse logUser(LoginRequest request);
}
