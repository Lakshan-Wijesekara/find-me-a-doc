package com.find_me_a_doc_backend.infrastructure.security.jwt;

import com.find_me_a_doc_backend.application.ports.TokenProvider;
import com.find_me_a_doc_backend.domain.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtProviderImpl implements TokenProvider {
    // This class acts as a digital ID
    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    private static final long JWT_EXPIRATION = 86400000; // 1 Day in milliseconds

    @Override
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    // This method generates the token for the user adds new claims and also add the role of the user to the token.
    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail()) // Assuming email is the unique identifier
                .claim("role", user.getRole()) // Inject role into token
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    // This method extracts the username from the token and returns it.
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // This ensures that the token is still valid and not passed the 24 hours.
    @Override
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}