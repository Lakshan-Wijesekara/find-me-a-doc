package com.findmeadoc.infrastructure.security.filter;

import com.findmeadoc.infrastructure.security.jwt.JwtProviderImpl;
import com.findmeadoc.infrastructure.security.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// To intercept the requests and check for the presence of a valid JWT token in the Authorization header
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProviderImpl jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtProviderImpl jwtProvider, CustomUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Look for the Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. If the header is missing or doesn't start with "Bearer ", pass to the next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (remove the "Bearer " prefix)
        jwt = authHeader.substring(7);
        userEmail = jwtProvider.extractUsername(jwt);

        // 4. If we found an email and the user is not already authenticated in this request
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. If the token is valid, officially authenticate the user for this request
            if (jwtProvider.isTokenValid(jwt, ((com.findmeadoc.infrastructure.security.services.CustomUserDetails) userDetails).getUser())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Save the authenticated user into Spring's security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue to the next step in the chain (e.g., your controllers)
        filterChain.doFilter(request, response);
    }
}