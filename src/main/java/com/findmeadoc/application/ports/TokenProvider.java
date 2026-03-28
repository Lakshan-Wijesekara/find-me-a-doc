package com.findmeadoc.application.ports;
import com.findmeadoc.domain.models.User;

// Using the interface for token provider and JWT related operations will be separated for separation of concerns.
public interface TokenProvider {
    String generateToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, User user);
}
